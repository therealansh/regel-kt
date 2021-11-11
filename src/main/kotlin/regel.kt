import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import java.io.File
import java.io.IOException
import java.util.*

class Regel : CliktCommand() {
    val model: String by option(help = "Pretrained Model").default("so")
    val benchmark = "customize"

    val top: Int by option(help = "Top results").int().default(5)
    val dir = System.getProperty("user.dir")
    val example_dir = "exp/interactive/$benchmark/example/1/"
    val example_cache_path = "exp/interactive/$benchmark/examples_cache/"
    val logsPath = "exp/interactive/$benchmark/logs/1"
    val benchmark_path = "exp/$benchmark/benchmark"

    fun prepareFolder() {
        if (!File(example_dir).exists()) File(example_dir).mkdirs()
        if (!File(example_cache_path).exists()) File(example_cache_path).mkdirs()
        if (!File(logsPath).exists()) File(logsPath).mkdirs()
        if (!File(benchmark_path).exists()) File(benchmark_path).mkdirs()
    }

    fun interactive() {
        prepareFolder()
        val output: MutableList<String> = mutableListOf()
        try {
            while (true) {
                println("=======================")
                println("Enter filename to save progress. Press 'Enter'")
                val fileName = readLine()!!.toString()
                if (fileName.isEmpty()) {
                    break
                }
                println("Enter the Natural Language(Press enter to continue): ")
                val nl = readLine().toString().trimEnd()

                val examples: MutableList<Pair<String, String>> = mutableListOf()

                while (true) {
                    println("Enter the example(Press enter to continue): ")
                    val ex = readLine()!!.toString().trimEnd()
                    if (ex.isEmpty()) {
                        break
                    }
                    println("Enter + for positive and - for negative example: ")
                    val sign = readLine()!!.toString()
                    examples.add(Pair(ex, sign))
                    // TODO Add caching
                }
                println("generating sketches...")
                //Default 25
                val sketch: List<String> = parseDescriptions(listOf<String>(nl), model, 25)
                println("Sketches:- \n $sketch")
                writeBenchmark(fileName, nl, examples)
                copyBenchmarkToInteractive(fileName)
                output += testBenchmark(fileName, nl, sketch, examples)

                if (output.size < 5) {
                    for (i in 0 until output.size) {
                        println("[${i}].  ${output[i]}")
                    }
                } else {
                    for (i in 0..4) {
                        println("[${i}].  ${output[i]}")
                    }
                    println("Any regex correct? [y/n]")
                    val response = readLine()!!.toString().trimEnd()
                    if (response.lowercase(Locale.getDefault()) == "n") {
                        println("All the outputs:\n")
                        for (i in 0 until output.size) {
                            println("[${i}].  ${output[i]}")
                        }
                    }
                }
            }
        } catch (err: Error) {
            writeOutput(output)
        }
    }

    private fun copyBenchmarkToInteractive(fileName: String) {
        File("exp/customize/benchmark/$fileName").let { it ->
            it.copyTo(
                File("exp/interactive/customize/example/1/$fileName"),
                overwrite = true
            )
        }
    }

    private fun writeBenchmark(fileName: String, nl: String, examples: MutableList<Pair<String, String>>) {
        val bench_path = "exp/customize/benchmark/$fileName"
        val bench_file = File(bench_path)
        bench_file.writeText("// natural language\n$nl \n\n")
        bench_file.appendText("// example")
        for (ex in examples) {
            bench_file.appendText("\n${ex.first},${ex.second}")
        }
        bench_file.appendText("\n\n")
        bench_file.appendText("// gt\nna")
    }

    private fun writeOutput(output: MutableList<String>) {
        val outputFile = "$logsPath/rawOutput.csv"
        for (i in output) {
            File(outputFile).appendText("$i\n")
        }
    }

    private fun testBenchmark(
        fileName: String,
        nl: String,
        sketch: List<String>,
        examples: MutableList<Pair<String, String>>
    ): MutableList<String> {
        val temp = mutableListOf<String>()
        println("NL: ${nl}")
        println("Example: \n")
        for (ex in examples) println(ex)
        println("Running the synthesizer...")
        var i = 1;
        for (sk in sketch) {

            val res: String = ResnaxRunner(
                args = listOf(
                    "0",
                    "$example_dir$fileName",
                    logsPath,
                    sk.toString(),
                    i++.toString(),
                    "1",
                    "0"
                )
            ).runStr()
            temp.add(res)
        }
        return temp
    }

    private fun parseDescriptions(desps: List<String>, model: String, i: Int): List<String> {
        val path = System.getProperty("user.dir")
        val tmp = "_tmp"
        val tmp_path = "sempre/dataset/${tmp}.raw.txt"
        val tmp_file = File(tmp_path)
        tmp_file.createNewFile()
        tmp_file.writeText("#\tNL\tsketch \n")
        desps.forEachIndexed { i, d -> tmp_file.appendText("$i \t $d \t null\n") }
        val sempre_path = "sempre/"
        System.setProperty("user.dir", sempre_path)
        val sketch_dir = File("outputs/$tmp/")
        if (!sketch_dir.exists()) sketch_dir.mkdirs()
        val command =
            "python3 py_scripts/test.py --dataset $tmp --model_dir pretrained_models/pretrained_$model --topk $i"
        val sketches = mutableListOf<String>()
        try {
            command.runCommand(File(sempre_path))
        } catch (e: IOException) {
            println(e)
        } finally {
            System.setProperty("user.dir", path)
            File("sempre/outputs/$tmp/").walk().forEach {
                if (it.isDirectory) return@forEach
                for (i in it.readLines()) {
                    sketches += i.split(" ")[1]
                }
            }
        }
        return sketches
    }

    private fun String.runCommand(workingDir: File) {
        ProcessBuilder(*split(" ").toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()
    }

    override fun run() {
        "ant -buildfile build.xml clean".runCommand(File("resnax/"))
        "ant -buildfile build.xml resnax".runCommand(File("resnax/"))
        interactive()
    }
}
