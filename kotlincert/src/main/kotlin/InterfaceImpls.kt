package okik.tech

import javax.xml.crypto.Data
import kotlin.properties.Delegates

interface shombo {
    fun ok()

    fun doSomething() {
        println("Shombo")
    }
}


class InterfaceImpls: shombo  {
    infix fun chip(inti : InterfaceImpls) = false

    override fun ok() {
        super<shombo>.doSomething()
    }

    override fun doSomething() {
        println("Shombo child")
    }
}

sealed interface my {
    class myA: my {

    }
}

// the two following are "interface delegation"
class shomboDelegator1(
    chomo: shombo
): shombo by chomo

class shomboDelegator2(
    var chomo: shombo
): shombo by chomo

// these are properties delegation(property delegates provided by kotlin "lazy", "")
 class shomboDelegator3 {
     // has to be a var because lazy interface doesn't implement a setValue() function
     // this will be initialized the first time it is accessed
     val chomo: shombo by lazy {
         InterfaceImpls()
     }

    // observable delegate
    val modifiableValue by Delegates.observable(chomo) { property, oldValue, newValue ->
        // log changes
        println("${property.name} changed from $oldValue to $newValue")
    }
 }