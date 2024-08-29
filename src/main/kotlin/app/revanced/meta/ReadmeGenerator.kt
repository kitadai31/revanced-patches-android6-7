package app.revanced.meta

import app.revanced.patcher.data.Context
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.description
import app.revanced.patcher.extensions.PatchExtensions.include
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.Patch
import java.io.File

internal class ReadmeGenerator : PatchesFileGenerator {
    private companion object {
        private const val TABLE_HEADER =
            "| \uD83D\uDC8A Patch | \uD83D\uDCDC Description | Default |\n" +
                    "|:--------:|:--------------:|:-------:|"
    }

    override fun generate(bundle: PatchBundlePatches) {
        val output = StringBuilder()
        output.appendLine("## \uD83E\uDDE9 Patches List\n")

        mutableMapOf<String, MutableList<Class<out Patch<Context<*>>>>>()
            .apply {
                for (patch in bundle) {
                    patch.compatiblePackages?.forEach { pkg ->
                        if (!contains(pkg.name)) put(pkg.name, mutableListOf())
                        this[pkg.name]!!.add(patch)
                    }
                }
            }
            .entries
            .sortedByDescending { it.value.size }
            .forEach { (`package`, patches) ->
                val mostCommonVersion = buildMap {
                    patches.forEach { patch ->
                        patch.compatiblePackages?.single { compatiblePackage -> compatiblePackage.name == `package` }?.versions?.let {
                            it.forEach { version -> merge(version, 1, Integer::sum) }
                        }
                    }
                }.let { commonMap ->
                    commonMap.maxByOrNull { it.value }?.value?.let {
                        // This is not foolproof, because for example v1.0.0-dev.0 will be returned instead of v1.0.0-release.
                        // Unfortunately this can not be solved easily because versioning can be complex.
                        commonMap.entries.filter { mostCommon -> mostCommon.value == it }.maxBy { it.key }.key
                    } ?: "all"
                }

                output.apply {
                    appendLine("### [\uD83D\uDCE6 `${`package`}`](https://play.google.com/store/apps/details?id=${`package`})")
                    appendLine("<details>\n")
                    appendLine("Target version: $mostCommonVersion\n")
                    appendLine(TABLE_HEADER)
                    patches.forEach { patch ->
                        appendLine(
                            "| `${patch.patchName}` " +
                                    "| ${patch.description} " +
                                    "| ${if (patch.include) "" else "No"} |"
                        )
                    }
                    append("</details>")
                }
            }

        File("README.md").run {
            writeText(
                readText().replace(Regex("## \uD83E\uDDE9 Patches List.*?</details>", RegexOption.DOT_MATCHES_ALL), output.toString())
            )
        }
    }
}