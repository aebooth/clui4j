package org.boof

import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.function.Supplier
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class ListItemInput(val caseSensitive:Boolean = false, var prompt: String = ""):Input<String>(){
        private val items = LinkedHashMap<String, String>()
        var failMessage:String = "That's not a valid choice. " +
                "Please try again."
        private var key:String? = null
        private var value:String? = null

        constructor(vararg itemList:Pair<String,String>):this(){
            val newItems:Array<Pair<String,String>> = Array(itemList.size,{ _:Int -> "" to ""})
            for ((index,item) in itemList.withIndex()) {
                newItems[index] =
                        if (!caseSensitive) (item.first.toLowerCase() to item.second.toLowerCase())
                        else item
            }
            this.items.putAll(newItems.toMap())
        }

        fun add(key:String,value:String) {
            if(!caseSensitive)this.items.put(key.toLowerCase(),value.toLowerCase())
            else this.items.put(key,value)
        }

        override fun get():String {
            return this.value ?: throw InputNotRunException()
        }

        fun getKey():String{
           return this.key ?: throw InputNotRunException()
        }

        fun getValue():String{
            return this.value ?: throw InputNotRunException()
        }

        override fun run(){
            var haveAnswer = false
            while (!haveAnswer) {
                if(prompt.length > 0) {
                    println(prompt)
                    println()
                }
                for (item in this.items) {
                    print(item.key + " : ")
                    println(item.value)
                }
                println()
                print(">>>")
                val userVal = if(!caseSensitive) this.input.nextLine().toLowerCase() else this.input.nextLine()
                if (userVal in items.keys) {
                    this.key = userVal
                    this.value = items[userVal] ?:value
                    haveAnswer = true
                } else if (userVal in items.values){
                    this.value = userVal
                    items.forEach { key, value -> if(value.equals(userVal)){this.key = key}}
                    haveAnswer = true
                }else{
                    println(this.failMessage)
                    continue
                }
            }
        }
    }


interface IOElement{
    fun run()
}

abstract class Input<T>:IOElement{
    val input = Scanner(System.`in`)
    abstract fun get():T
}

open class TextInput(open var prompt: String = ""):Input<String>(){
    private var result:String? = null
    override fun get():String {
        return result ?: throw InputNotRunException()
    }

    override fun run() {
        print(prompt)
        result = input.nextLine()
    }
}

class FlagInput(var prompt: String = "",val default:Char? = null, vararg val flags:Char):Input<Char>(){
    private var result:Char? = null

    override fun get():Char{
        return result ?: throw InputNotRunException()
    }

    override fun run() {
        val end = StringBuilder()
        end.append(if(default != null) default.toString().toUpperCase() else "")
        var remaining:List<Char> = flags.filter { f -> f != null && f != default }
        for(flag in remaining) end.append(flag)
        var haveAnswer:Boolean = false
        while(!haveAnswer) {
            print(prompt + " ${end.toString()}?")
            try{
                var response = input.next()
                if(response.length == 0) {
                    if (default != null) result = default
                    else print("No there is no default input--please select a flag!")
                    continue
                }
                else if(response.length == 1 &&
                            (response as Char in flags || response as Char == default?.toUpperCase() as Char)){
                    result = response as Char
                    haveAnswer = true
                }else{
                        print("Input does not match recognized flag!")
                        continue
                }
            }
        }
    }
}



class NumberInput(var prompt:String = ""):Input<Number>(){
    private var result:Number? = null
    private var type:String = "Double"
    var failMessage = "User input is not a Double. Input value must be a Double!"

    fun setNumberType(type:String){
        if(type in listOf("Double","Integer","Byte","Short","Float","Long")){
            this.type = type
            this.failMessage = "User input is not a ${this.type}. Input value must be a ${this.type}!"
        }else{
            throw Exception("type must be a valid Number type!")
        }
    }

    override fun get():Number{
        return result ?: throw InputNotRunException()
    }

    override fun run(){
        var haveAnswer = false
        while(!haveAnswer){
            print(prompt)
            val response = input.nextLine()
            try {
                when(this.type){
                    "Double" -> result = response.toDouble()
                    "Integer" -> result = response.toInt()
                    "Byte" -> result = response.toByte()
                    "Short" -> result = response.toShort()
                    "Float" -> result = response.toFloat()
                    "Long" -> result = response.toLong()
                }
            } catch (e: Exception) {
                println(failMessage)
                continue
            }
            haveAnswer = true
        }

    }
}

class Output(text:String? = null):IOElement{
    val texts:ArrayList<Supplier<String>> = ArrayList()
    init{
        if(text != null) texts.add(Supplier{text as String})
    }

    constructor(futureText:Supplier<String>):this(null){
        texts.add(futureText)
    }

    fun append(text:String){
        this.texts.add(Supplier{->text})
    }

    fun append(futureText:Supplier<String>){
        this.texts.add(futureText)
    }

    fun newLine(){
        this.append("\n")
    }

    override fun run(){
        var result:StringBuilder = StringBuilder()
        for(text in this.texts){
            result.append(text.get())
        }
        println(result.toString())
    }
}

class UI(vararg members:IOElement):IOElement{
    private val items = ArrayList<IOElement>()
    init{
        this.items.addAll(members)
    }

    fun add(vararg items:IOElement){
        for(item in items){
            this.items.add(item)
        }
    }

    fun remove(vararg items:IOElement){
        for(item in items){
            this.items.remove(item)
        }
    }

    override fun run(){
        for(item in this.items){
            item.run()
        }
    }
}

class InputNotRunException:Exception("This input has not been run yet!")
