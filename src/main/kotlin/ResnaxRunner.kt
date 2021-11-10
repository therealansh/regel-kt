import resnax.Main
class ResnaxRunner(val args: List<String>) {

    fun runIO(){
        Main.main(args.toTypedArray())
    }

    fun runStr(): String {
        return Main.mainRegel(args.toTypedArray())
    }
}