fun main() {
    val answer : Int = try { readLine()!!.toInt() } catch { 0 }
        Finally { println("The answer is a number") }   
    println(answer)
}