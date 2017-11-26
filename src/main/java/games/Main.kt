package org.hiucimon.games

import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import tornadofx.*
import java.io.File

val defaultLocation="/Users/joe/Downloads/pcgen2/data"

typealias ALS=ArrayList<String>

class Main : View() {

    override val root: ScrollPane by fxml()
    val sp:ScrollPane by fxid()
    val tf: TextField by fxid()
    val myLabel: Label by fxid()
    val gameModes: ListView<String> by fxid()
    var base= File(defaultLocation)
    init {
//        myLabel.text = tf.text
        with (root) {
            tf.setOnInputMethodTextChanged {
                tf.text = defaultLocation
            }
            loadGameModes()
        }
    }
    fun onClick() {
        loadGameModes()
    }
    fun checkBaseDir() {
        val test=File(tf.text)
        if (test.exists() and test.isDirectory) {
            myLabel.text="Valid"
            base=test
//            loadGameModes()
        } else {
            myLabel.text="Invalid"
        }
    }
    fun loadGameModes() {
        gameModes.items.clear()
        val work= sortedSetOf<String>()
        base.walkBottomUp().forEach {
            if (it.name.endsWith(".pcc")) {
                val res=parsePCCHeader(it)
                work.add(res.toString())
            }
        }
        gameModes.items.addAll(work)
    }
    fun parsePCCHeader(fin:File):pccInfo {
        val lookfor=hashMapOf<String,Int>("CAMPAIGN" to 0,
                "GAMEMODE" to 0,
                "RANK" to 0,
                "GENRE" to 0,
                "BOOKTYPE" to 0,
                "SETTING" to 0,
                "TYPE" to 0,
                "PUBNAMELONG" to 0,
                "PUBNAMESHORT" to 0,
                "PUBNAMEWEB" to 0,
                "SOURCELONG" to 0,
                "SOURCESHORT" to 0,
                "SOURCEWEB" to 0,
                "SOURCEDATE" to 0,
                "ISOGL" to 0,
                "COPYRIGHT" to 0)
        var work=pccInfo(fileName = fin.absolutePath)
        val rx=Regex("^(\\w+):(.*)")
        fin.forEachLine {
            val m=rx.matchEntire(it)
            if (m!=null) {
                val t=m.destructured.component1()
                if (lookfor.containsKey(t)) {
                    when (t) {
                        "CAMPAIGN"->work.campaign=m.destructured.component2()
                        "GAMEMODE"->work.gameMode=m.destructured.component2()
                        "RANK"->work.rank=m.destructured.component2().toInt()
                        "GENRE"->work.genre=m.destructured.component2()
                        "BOOKTYPE"->work.bookType=m.destructured.component2()
                        "SETTING"->work.setting=m.destructured.component2()
                        "TYPE"->work.gameType=m.destructured.component2()
                        "PUBNAMELONG"->work.pubNameLong=m.destructured.component2()
                        "PUBNAMESHORT"->work.pubNameShort=m.destructured.component2()
                        "PUBNAMEWEB"->work.pubNameWeb=m.destructured.component2()
                        "SOURCELONG"->work.sourceLong=m.destructured.component2()
                        "SOURCESHORT"->work.sourceShort=m.destructured.component2()
                        "SOURCEWEB"->work.sourceWeb=m.destructured.component2()
                        "SOURCEDATE"->work.sourceDate=m.destructured.component2()
                        "ISOGL"->work.isoGL=m.destructured.component2()
                        "COPYRIGHT"->work.copywrite.add(m.destructured.component2())
                    }
                }
            }
        }
        return work
    }
}


data class pccInfo(var gameMode:String="",var campaign:String="",var rank:Int=0,var genre:String="",var bookType:String="",var setting:String="",
                   var gameType:String="",var pubNameLong:String="",var pubNameShort:String="",var pubNameWeb:String="http://",
                   var sourceLong:String="",var sourceShort:String="",var sourceWeb:String="http://",var sourceDate:String="",
                   var isoGL:String="",var fileName:String,var copywrite:ALS=arrayListOf<String>())