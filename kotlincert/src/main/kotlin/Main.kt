package okik.tech

import java.io.File
import java.lang.Thread.yield
import kotlin.UByte
import kotlin.io.path.writeLines
import kotlin.random.Random

val myfun: () -> String = { "" }
var some = ""

 internal var Int.mia: Boolean
    get() = true
    set(value) {
        mia = value
    }

internal infix fun Int.chi(other: Int) = false

inline fun <reified T> Array<T>.nonEmptyList(): List<T?> {
    if (size != 0) return toList()

    val result: T? = T::class.java.declaredConstructors
        .filter { construct -> construct.parameterCount == 0 }
        .take(1)
        .map { constructor -> constructor.newInstance() as T }
        .firstOrNull()

    return listOf(result)
}

enum class Prueba {
    UNO {
        override infix fun infixExample(inti : Any) {

        }

        override fun mustImplement() {
            println("uno must implement")
        }

        override var absFromParent: String = "uno abstractImpl"
    },
    DOS {
        override val fromParent = "leslin dos"
        override var absFromParent: String = "dos abstractImpl"

        fun doSomething() {
            println("dos Do something")
        }

        override infix fun infixExample(inti : Any) {

        }

        override fun papaDoSomething() {
            println("dos child Do something")
        }

        override fun mustImplement() {
            println("dos must implement")
        }
    };

    open fun papaDoSomething() {
        println("papa Do something")
    }

    fun closedDoSomething() {
        println("can't be overriden")
    }

    abstract fun mustImplement()

    open val fromParent = "hola"

    abstract var absFromParent : String

    abstract infix fun infixExample(inti : Any)
}

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main(array: Array<String>) {
    println(arrayOf<Int>().nonEmptyList())

    Prueba.UNO.closedDoSomething()
    Prueba.DOS.closedDoSomething()

    val numbe: UByte = 255u
    val tw: UShort = 60000u
    val th: UInt = 60000u
    val ft: ULong = (-60000L).toULong()
    val sx: Long = ft.toUByte().toLong()
    print("""
    hola
    do
        si
        no
            |m
    mo    """.trimMargin())

    var prueba = Prueba.UNO
    mapeo(prueba)

    var a0 = emptyArray<Int>()
    var a1 = arrayOf(1,2,3,4,5, null)
    var a2 = arrayOfNulls<Int>(5)
    var a3 = Array(5) { IntArray(5) }
    var a4 = Array(5) { it }
    var a5 = IntArray(5)
    var a6 = IntArray(5) { index -> index }
    var a7 = intArrayOf(20, 54)

    var l0 = emptyList<String>()
    var l1 = listOf(1,2,3,4,5,null)
    var l2 = listOfNotNull(1,2,3,4,5,null)
    var l3 = List(5) { it }
    var l5: List<Unit> = MutableList(5) {}
    var l6: MutableList<Int> = MutableList(5) { index -> index }
    var l7 = buildList(5) {
        add(5)
    }

    // no set can call "get" just like in Java
    var s1 = setOf(67, 5, 68) // immutable hash set can't call "add" nor "remove"
    for (s in s1) {
        println("del set: $s")
    }
    var s2 = setOfNotNull(null,67,67)
    var s3 = emptySet<Int>()
    // mutableSetOf it returns a linkedHashSet and you actually should be careful with it because
    // linkedHash/linked collections create more objects under the hood to  maintain insertion order, this is true for all
    // mutable collections in Kotlin, set, map.
    var s4 = mutableSetOf(2, 3)
    var s5 = buildSet(5) {
        add(5)
    }
    var s7 = hashSetOf(5, 7) // mutable hash set which means you can add and remove
    var s8 = hashSetOf<Int>()
    var s9 = linkedSetOf(1,2) // mutable linked hash set which means insertion order is persisted(be careful)
    var s10 = linkedSetOf<Int>()
    var s11 = sortedSetOf(100, 22) // a mutable sorted set(TreeSet), you can pass a comparator or the object must implement comparable
    var s12 = sortedSetOf<Int>()
    var s13 = _root_ide_package_.kotlin.collections.HashSet<Int>() // directly using java hashmap under the hood

    // map follow the same creation patterns(maps doesn't have the "get" limitation as opposed to sets)
    var m1 = mapOf(Pair(1,6), 3 to 4, 5 to 7, 6 to 1)

    m1
        .toSortedMap({ current, next -> m1.get(current)!!.compareTo(m1.get(next)!!) } )
        .toList()
        .take(2)
        .forEach { entry -> println("with sorter map id: ${entry.first} grade ${entry.second}") }

    m1
        .toList()
        .sortedBy { entry -> entry.second }
        .take(2)
        .forEach { entry -> println("id: ${entry.first} grade ${entry.second}") }

    var seq1 = emptySequence<Int>()
    var seq2 = sequenceOf(1,2,3,4,5,null)

    var outerSeed = 1
    var seq3 = generateSequence {
        ++outerSeed
    }

    var seq4 = generateSequence(0) { seed ->
        val saveNext = outerSeed
        outerSeed = seed + outerSeed
        saveNext
    } // this would be a fibonacci
    var seq5 = generateSequence({ outerSeed++ }) { seed -> seed * 2 }

    var seq6 = Sequence {
        iterator {
            yield() // doesn't count
            yield(2)

            while (true) {
                yield(outerSeed++)
            }
        }
    }

    var seq7 = sequence {
        while (true) {
            yield(outerSeed++)
        }
    }

    seq6.take(3).forEach { num -> println("from seq: $num") }

    val mapi = File("myFile").readLines().map { line ->
        val lin = line.split("")
        lin[0] to lin[1]
    }

    File("myFile").toPath().writeLines(listOf("uno", "dos", "dos", "dos"))

    val child: Parent = Child()

    child.doSomething()

    ViewModel.Single.MAX
    ViewModel.Single.doSomething()
}

fun randonNumberGenerator(max: Int) = Random.nextInt(max)

fun mapeo(prueba: Any) = when {
    prueba is Prueba -> 1
    prueba is Int -> 2
    else -> 3
}