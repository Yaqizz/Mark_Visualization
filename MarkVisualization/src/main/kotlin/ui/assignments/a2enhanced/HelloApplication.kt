package ui.assignments.a2enhanced

import javafx.application.Application
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt


// top
var new_name = ""
var new_term = ""
var new_grade = ""
val top_name = TextField().apply {
    prefWidth = 90.0
    minWidth = 90.0
    maxWidth = Double.MAX_VALUE
    HBox.setHgrow(this, Priority.NEVER)
    textProperty().addListener{ _,_, newValue ->
        new_name = newValue
    }
}
val top_term = ChoiceBox<String>().apply{
    items.addAll("F20", "W21", "S21","F21", "W22", "S22","F22", "W23", "S23","F23")
    valueProperty().addListener { _,_, newValue ->
        when(newValue) {
            "F20" -> new_term = "F20"
            "W21" -> new_term = "W21"
            "S21" -> new_term = "S21"
            "F21" -> new_term = "F21"
            "W22" -> new_term = "W22"
            "S22" -> new_term = "S22"
            "F22" -> new_term = "F22"
            "W23" -> new_term = "W23"
            "S23" -> new_term = "S23"
            "F23" -> new_term = "F23"
        }
    }
}
val top_grade = TextField().apply {
    prefWidth = 50.0
    minWidth = 50.0
    maxWidth = Double.MAX_VALUE
    HBox.setHgrow(this, Priority.NEVER)
    textProperty().addListener{ _,_, newValue ->
        new_grade = newValue
    }
}

// centre
var mylist = mutableListOf<Courses>()
var term_list = mutableListOf<String>()
class Courses(name: String, term: String, grade: String) {

    var courseName = TextField(name).apply {
        prefWidth = 60.0
        minWidth = 60.0
        maxWidth = Double.MAX_VALUE
        HBox.setHgrow(this, Priority.NEVER)
        isEditable = false
    }
    var courseTerm = ChoiceBox<String>().apply{
        items.addAll("F20", "W21", "S21","F21", "W22", "S22","F22", "W23", "S23","F23")
        when(new_term.take(3)) {
            "F20" -> value = "F20"
            "W21" -> value = "W21"
            "S21" -> value = "S21"
            "F21" -> value = "F21"
            "W22" -> value = "W22"
            "S22" -> value = "S22"
            "F22" -> value = "F22"
            "W23" -> value = "W23"
            "S23" -> value = "S23"
            "F23" -> value = "F23"
        }
        valueProperty().addListener{_, _, _ ->
            ctrlUpdate.apply { isDisable = false }
            ctrlDelete.apply { isVisible = false}
            ctrlUndo.apply { isVisible = true }
        }
    }
    var courseGrade = TextField(grade).apply {
        prefWidth = 40.0
        minWidth = 40.0
        maxWidth = Double.MAX_VALUE
        HBox.setHgrow(this, Priority.NEVER)
        textProperty().addListener { _, _, _ ->
            ctrlUpdate.apply { isDisable = false }
            ctrlDelete.apply { isVisible = false }
            ctrlUndo.apply { isVisible = true }
        }
    }
    var ctrlUpdate = ControllerUpdate(name, term, grade)
    var ctrlDelete = ControllerDelete(name, term, grade)
    var ctrlUndo = ControllerUndo(name, term, grade, term, grade)
    var course = ToolBar(courseName, courseTerm, courseGrade, ctrlUpdate, Group(ctrlDelete, ctrlUndo)).apply{
        setColor(this, new_grade)
    }
}

// helper function for find index of list
fun myfind(list: MutableList<Courses>, target: Courses): Int {
    list.forEach{
        if (it.courseName.text == target.courseName.text) {
            return list.indexOf(it)
        }
    }
    return -1
}

// helper function for set background color
fun setColor(t: ToolBar, grade: String) {
    if (grade == "WD") {
        t.background = Background(BackgroundFill(Color.DARKSLATEGRAY, CornerRadii(5.0), null))
    } else {
        when (grade.toInt()) {
            in 0..49 -> {
                t.background = Background(BackgroundFill(Color.LIGHTCORAL, CornerRadii(5.0), null))
            }
            in 50..59 -> {
                t.background = Background(BackgroundFill(Color.LIGHTBLUE, CornerRadii(5.0), null))
            }
            in 60..90 -> {
                t.background = Background(BackgroundFill(Color.LIGHTGREEN, CornerRadii(5.0), null))
            }
            in 91..95 -> {
                t.background = Background(BackgroundFill(Color.SILVER, CornerRadii(5.0), null))
            }
            in 96..100 -> {
                t.background = Background(BackgroundFill(Color.GOLD, CornerRadii(5.0), null))
            }
        }
    }
}

// make Vbox of the course list
fun makelist(uselist: MutableList<Courses>): VBox? {
    var result: VBox? = null
    uselist.forEach {
        result = if (result == null) {
            VBox(uselist[0].course).apply {
                padding = Insets(5.0, 0.0, 5.0, 0.0)
            }
        } else {
            VBox(it.course, result).apply {
                padding = Insets(5.0, 0.0, 5.0, 0.0)
            }
        }
    }
    return result
}

// add term in term_list
fun termAdd(list: MutableList<String>, t: String): MutableList<String> {
    list.add(t)
    list.sortWith(compareBy<String> {
        it.substring(1).toInt()
    }.thenByDescending { it.take(1) })
    return list
}

// return a list of term starting from f to l
fun xlist(f: String, l: String): MutableList<String> {
    val list = mutableListOf("F20", "W21", "S21","F21", "W22", "S22","F22", "W23", "S23","F23")
    val first = list.indexOf(f)
    val last = list.indexOf(l)
    return list.subList(first, last)
}

// return average grade of the term
fun termAva(list: MutableList<Courses>, term: String): Double {
    var total = 0
    var num = 0
    list.forEach {
        if (it.courseTerm.value == term) {
            val g = it.courseGrade.text
            if (g != "WD") {
                total += g.toInt()
                num++
            }
        }
    }
    val ava = if (num == 0) { -1.0 } else {
        val dou = total.toDouble() / num.toDouble()
        (dou * 10.0).roundToInt() / 10.0
    }
    return ava
}

// return the number of courses with the course code beginning with s
fun count(list: MutableList<Courses>, s: String): Int {
    val len = s.length
    var result = 0
    list.forEach {
        if (it.courseName.text.take(len) == s) {
            result++
        }
    }
    return result
}

// set the rectangles
fun initRect() {
    val w = Model.root.width - 350.0
    val h = Model.root.height
    v2.graphicsContext2D.apply {
        fill = Color.LIGHTYELLOW
        fillRect(0.17 * w, 0.17 * h - 17.5, 0.396 * w, 35.0)
        fill = Color.LIGHTPINK
        fillRect(0.17 * w, 0.17 * h + 0.18 * h - 17.5, 0.144 * w, 35.0)
        fill = Color.LIGHTGRAY
        fillRect(0.17 * w, 0.17 * h + 0.36 * h - 17.5, 0.18 * w, 35.0)
        fill = Color.LIGHTGREEN
        fillRect(0.17 * w, 0.17 * h + 0.54 * h - 17.5, 0.72 * w, 35.0)
    }
}

// return the list of the number of courses in different grade interval
fun gList(list: MutableList<Courses>): List<Int> {
    var grade49 = 0
    var grade59 = 0
    var grade90 = 0
    var grade95 = 0
    var grade100 = 0
    var gradewd = 0
    list.forEach {
        if (it.courseGrade.text == "WD") {
            gradewd++
        } else {
            when(it.courseGrade.text.toInt()) {
                in 0..49 -> { grade49++}
                in 50..59 -> { grade59++ }
                in 60..90 -> { grade90++}
                in 91..95 -> { grade95++}
                in 96..100 -> { grade100++}
            }
        }
    }
    return listOf(gradewd, grade49, grade59, grade90, grade95, grade100)
}

// draw grid in visualization
fun drawGrid(gc: GraphicsContext, w: Double, h: Double) {
    // grid
    gc.apply {
        // grid
        stroke = Color.LIGHTGRAY
        (0..9).forEach {
            val y = 0.09 * h + it * 0.07 * h
            strokeLine(0.09 * w, y, 0.9 * w, y)
        }
        stroke = Color.BLACK
        lineWidth = 1.0
        // y value
        var text = 100
        (0..9).forEach {
            val y = 0.09 * h + it * 0.07 * h
            fill = Color.BLACK
            font = Font.font("Arial", 15.0)
            fillText(text.toString(), 0.05 * w, y)
            text -= 10
        }
        fillText(text.toString(), 0.05 * w, 0.8 * h)
        // coordinate
        lineWidth = 2.0
        strokeLine(0.09 * w, 0.8 * h, 0.9 * w, 0.8 * h)
        strokeLine(0.09 * w, 0.8 * h, 0.09 * w, 0.09 * h)
    }
}

// write the text of corresponding course names
fun drawtext(gc: GraphicsContext, target: String) {
    if (target == "") {
        Model.visual3()
        Model.visual4()
        return
    }
    Model.visual3()
    Model.visual4()

    gc.fill = Color.BLACK
    gc.font = Font.font("Arial", 15.0)

    val yValue = 50.0
    var num: Int

    when (target) {
        "WD" -> {
            num = 0
            mylist.forEach {
                if (it.courseGrade.text == "WD") {
                    gc.fillText(it.courseName.text, 50.0,yValue + num * 15.0)
                    num++
                }
            }
        }
        "49" -> {
            num = 0
            mylist.forEach {
                if (it.courseGrade.text != "WD") {
                    val g = it.courseGrade.text.toInt()
                    if (g in 0..49) {
                        gc.fillText(it.courseName.text, 50.0, yValue + num * 15.0)
                        num++
                    }
                }
            }
        }
        "59" -> {
            num = 0
            mylist.forEach {
                if (it.courseGrade.text != "WD") {
                    val g = it.courseGrade.text.toInt()
                    if (g in 50..59) {
                        gc.fillText(it.courseName.text, 50.0, yValue + num * 15.0)
                        num++
                    }
                }
            }
        }
        "90" -> {
            num = 0
            mylist.forEach {
                if (it.courseGrade.text != "WD") {
                    val g = it.courseGrade.text.toInt()
                    if (g in 60..90) {
                        gc.fillText(it.courseName.text, 50.0, yValue + num * 15.0)
                        num++
                    }
                }
            }
        }
        "95" -> {
            num = 0
            mylist.forEach {
                if (it.courseGrade.text != "WD") {
                    val g = it.courseGrade.text.toInt()
                    if (g in 91..95) {
                        gc.fillText(it.courseName.text, 50.0, yValue + num * 15.0)
                        num++
                    }
                }
            }
        }
        "100" -> {
            num = 0
            mylist.forEach {
                if (it.courseGrade.text != "WD") {
                    val g = it.courseGrade.text.toInt()
                    if (g in 96..100) {
                        gc.fillText(it.courseName.text, 50.0, yValue + num * 15.0)
                        num++
                    }
                }
            }
        }
    }
}

// return the list of garde of the term
fun findList(list: MutableList<Courses>, t: String): MutableList<Double> {
    val result = mutableListOf<Double>()
    list.forEach {
        if (it.courseTerm.value == t && it.courseGrade.text != "WD") {
            result.add(it.courseGrade.text.toDouble())
        }
    }
    return result
}

// return the average value in list
fun findAva(list: MutableList<Double>): Double {
    var sum = 0.0
    list.forEach { sum += it }
    return sum / list.size
}

// find the sd
fun findSD(list: MutableList<Double>): Double {
    if (list.size == 0) return 0.0
    var sum = 0.0
    var sd = 0.0
    list.forEach { sum += it }
    val mean = sum / list.size
    list.forEach { sd += (it - mean).pow(2.0) }
    return sqrt(sd / (list.size - 1))
}

// fill the term in v1 and v4
fun fillxvalue(gc: GraphicsContext, termlist: MutableList<String>,
               map: MutableMap<String, Double>, w: Double, h: Double): MutableList<String> {
    var xList: MutableList<String>
    gc.apply {
        val firstT = termlist[0]
        val lastT = termlist[termlist.size - 1]
        xList = xlist(firstT, lastT)
        xList.add(lastT)
        val xLen = xList.size
        lineWidth = 1.0
        (0 until xLen).forEach {
            val x = 0.09 * w + (it + 1) * ((0.81 * w) / xLen) - 0.02 * w
            fill = Color.BLACK
            font = Font.font("Arial", 15.0)
            fillText(xList[it], x, 0.85 * h)
            map[xList[it]] = x
        }
    }
    return xList
}

// write legend text
fun writeLegendText(gc: GraphicsContext, x: Double, y:Double, s: String) {
    gc.apply {
        fillRect(x, y, 30.0, 15.0)
        fill = Color.BLACK
        font = Font.font("Arial", 15.0)
        fillText(s, x + 33.0, y + 13.0)
    }
}

// draw legend
fun legend(gc: GraphicsContext, x: Double, y:Double) {
    gc.fill = Color.DARKSLATEGRAY
    writeLegendText(gc, x, y, "WD")
    gc.fill = Color.LIGHTCORAL
    writeLegendText(gc, x, y - 20.0, "Failed")
    gc.fill = Color.LIGHTBLUE
    writeLegendText(gc, x, y - 40.0, "Low")
    gc.fill = Color.LIGHTGREEN
    writeLegendText(gc, x, y - 60.0, "Good")
    gc.fill = Color.SILVER
    writeLegendText(gc, x, y - 80.0, "Great")
    gc.fill = Color.GOLD
    writeLegendText(gc, x, y - 100.0, "Excellent")
    gc.fill = Color.BLACK
    gc.strokeLine(x - 5.0, y + 20.0, x - 5.0, y - 105.0)
    gc.strokeLine(x + 100.0, y + 20.0, x + 100.0, y - 105.0)
    gc.strokeLine(x - 5.0, y + 20.0, x + 100.0, y + 20.0)
    gc.strokeLine(x - 5.0, y - 105.0, x + 100.0, y - 105.0)
}


// MVC
// Model
object Model: Observable {
    private val listeners = mutableListOf<InvalidationListener?>()

    override fun addListener(listener: InvalidationListener?) {
        listeners.add(listener)
    }
    override fun removeListener(listener: InvalidationListener?) {
        listeners.remove(listener)
    }
    fun add() {
        if (new_name == "" || new_term == "" || new_grade == "") {
            return
        } else {
            if (new_grade != "WD") {
                val value = new_grade.toInt()
                if (value < 0 || value > 100) {
                    return
                }
            }
            mylist.add(Courses(new_name, new_term, new_grade))
        }
        // sort by term
        mylist.sortWith(compareBy<Courses> {
            it.courseTerm.value.substring(1).toInt()
        }.thenByDescending { it.courseTerm.value.take(1) })
        // add new term in term_list
        term_list = termAdd(term_list, new_term)
    }
    fun update(name: String, term: String, grade: String) {
        val curr = Courses(name, term, grade)
        val idx = myfind(mylist, curr)
        val g = mylist[idx].courseGrade.text
        mylist[idx].course.apply {
            setColor(this, g)
        }
        mylist[idx].ctrlUpdate.isDisable = true
        mylist[idx].ctrlDelete.isVisible = true
        mylist[idx].ctrlUndo.isVisible = false
        term_list.remove(term)
        term_list = termAdd(term_list, mylist[idx].courseTerm.value)
        //listeners.forEach{it?.invalidated(this)}
    }
    fun delete(name: String, term: String, grade: String) {
        val curr = Courses(name, term, grade)
        val idx = myfind(mylist, curr)
        term_list.remove(mylist[idx].courseTerm.value)
        mylist.remove(mylist[idx])

        //listeners.forEach{it?.invalidated(this)}
    }
    fun undo(name: String, term: String, grade: String, t: String, g: String) {
        val curr = Courses(name, term, grade)
        val idx = myfind(mylist, curr)
        mylist[idx].courseTerm.value = t
        mylist[idx].courseGrade.text = g
        mylist[idx].ctrlUpdate.isDisable = true
        mylist[idx].ctrlDelete.isVisible = true
        mylist[idx].ctrlUndo.isVisible = false
        listeners.forEach{it?.invalidated(this)}
    }

    // bottom
    private var bottom1_value = 0.0
    fun below1() {
        var avaTotal = 0
        var avaNum = 0
        mylist.forEach {
            val d = it.courseGrade.text
            if (d !="WD") {
                avaTotal += d.toInt()
                avaNum++
            }
        }
        bottom1_value = if (avaNum == 0) { 0.0
        } else {
            val dou = avaTotal.toDouble() / avaNum.toDouble()
            (dou * 10.0).roundToInt() / 10.0
        }
        //listeners.forEach{it?.invalidated(this) }
    }
    fun getbelow1(): Double {
        return bottom1_value
    }

    private var bottom2_value = 0
    fun below2() {
        var result2 = 0
        mylist.forEach {
            val d = it.courseGrade.text
            if (d != "WD") { result2++ }
        }
        bottom2_value = result2
        //listeners.forEach{it?.invalidated(this) }
    }
    fun getbelow2(): Int {
        return bottom2_value
    }

    private var bottom3_value = 0
    fun below3() {
        var reuslt3 = 0
        mylist.forEach {
            val d = it.courseGrade.text
            if (d != "WD") {
                if (d.toInt() < 50) {
                    reuslt3++
                }
            }
        }
        bottom3_value = reuslt3
        //listeners.forEach{it?.invalidated(this) }
    }
    fun getbelow3(): Int {
        return bottom3_value
    }

    private var bottom4_value = 0
    fun below4() {
        var reuslt4 = 0
        mylist.forEach {
            if (it.courseGrade.text == "WD") { reuslt4++ }
        }
        bottom4_value = reuslt4
    }
    fun getbelow4(): Int {
        return bottom4_value
    }

    val root = BorderPane(HBox(VBox(top_toolbar, center_part), visualization),
        null, null, bottom_part, null)

    // visualization1
    fun visual1() {
        v1.graphicsContext2D.apply {
            clearRect(0.0, 0.0, root.width, root.height)
            val w = root.width - 350.0
            val h = root.height
            drawGrid(this, w, h)
        }
        //listeners.forEach{it?.invalidated(this) }
    }

    // visualization2
    fun visual2() {
        v2.graphicsContext2D.apply {
            clearRect(0.0, 0.0, root.width, root.height)
            val w = root.width - 350.0
            val h = root.height
            (0..4).forEach {
                strokeLine(0.17 * w + it * 0.18 * w, 0.8 * h, 0.17 * w  + it * 0.18 * w, 0.07 * h)
                val i = it * 5
                fill = Color.BLACK
                font = Font.font("Arial", 15.0)
                fillText(i.toDouble().toString(),0.15 * w + it * 0.18 * w, 0.83 * h)
            }
            fillText("CS",0.07 * w, 0.17 * h)
            fillText("MATH",0.07 * w, 0.17 * h + 0.18 * h)
            fillText("Other",0.07 * w, 0.17 * h + 0.36 * h)
            fillText("Total",0.07 * w, 0.17 * h + 0.55 * h)

            // rectangle
            initRect()
        }
        //listeners.forEach{it?.invalidated(this) }
    }

    // visualization3
    fun visual3() {
        canvas3.graphicsContext2D.apply {
            clearRect(0.0, 0.0, root.width, root.height - 70.0)
            val w = root.width - 350.0
            val h = root.height - 70.0
            strokeOval(0.223 * w, 0.07 * h, 0.56 * w, 0.56 * w)
        }
        //listeners.forEach{it?.invalidated(this) }
    }

    // visualization4
    fun visual4() {
        v4.graphicsContext2D.apply {
            clearRect(0.0, 0.0, root.width, root.height)
            val w = root.width - 350.0
            val h = root.height
            drawGrid(this, w, h)
        }
        listeners.forEach{it?.invalidated(this) }
    }
}

// Controller
interface IController
// Controller of create button
class ControllerCreate: Button("Create"), IController {
    init {
        onAction = EventHandler {
            Model.add()
            Model.below1()
            Model.below2()
            Model.below3()
            Model.below4()
            Model.visual1()
            Model.visual2()
            Model.visual3()
            Model.visual4()
        }
        textAlignment = TextAlignment.CENTER
        prefWidth = 70.0
        minWidth = 70.0
        maxWidth = Double.MAX_VALUE
        HBox.setHgrow(this, Priority.NEVER)
    }
}
// Controller of update button
class ControllerUpdate(name: String, term: String, grade: String): Button("Update"),
    IController {
    init {
        onAction = EventHandler {
            Model.update(name, term, grade)
            Model.below1()
            Model.below2()
            Model.below3()
            Model.below4()
            Model.visual1()
            Model.visual2()
            Model.visual3()
            Model.visual4()
        }
        textAlignment = TextAlignment.CENTER
        prefWidth = 60.0
        minWidth = 60.0
        maxWidth = Double.MAX_VALUE
        HBox.setHgrow(this, Priority.NEVER)
        isDisable = true
    }
}
// Controller of delete button
class ControllerDelete(name: String, term: String, grade: String): Button("Delete"), IController {
    init {
        onAction = EventHandler {
            Model.delete(name, term, grade)
            Model.below1()
            Model.below2()
            Model.below3()
            Model.below4()
            Model.visual1()
            Model.visual2()
            Model.visual3()
            Model.visual4()
        }
        textAlignment = TextAlignment.CENTER
        prefWidth = 60.0
        minWidth = 60.0
        maxWidth = Double.MAX_VALUE
        HBox.setHgrow(this, Priority.NEVER)
        isVisible = true
    }
}
// Controller of undo button
class ControllerUndo(name: String, term: String, grade: String, t: String, g: String): Button("Undo"), IController {
    init {
        onAction = EventHandler {
            Model.undo(name, term, grade, t, g)
        }
        textAlignment = TextAlignment.CENTER
        prefWidth = 60.0
        minWidth = 60.0
        maxWidth = Double.MAX_VALUE
        HBox.setHgrow(this, Priority.NEVER)
        isVisible = false
    }
}
// Controller of missing courses
class ControllerMC: CheckBox("Include missing courses"), IController {
    init {
        onAction = EventHandler {
            Model.visual3()
            Model.visual4()
        }
        isSelected = false
    }
}

// View
class View : ScrollPane(), InvalidationListener {
    init {
        Model.addListener(this)
        content = null
        this.apply {
            vbarPolicy = ScrollBarPolicy.AS_NEEDED
            isFitToWidth = true
            isFitToHeight = true
            prefWidth = 350.0
            minWidth = 350.0

            minHeight = 385.0
            prefHeight = 1000.0
            maxHeight = Double.MAX_VALUE
        }
    }
    override fun invalidated(observable: Observable?) {
        content = makelist(mylist)
    }
}
// View of average grade
class Viewava: Label(), InvalidationListener {
    init {
        Model.addListener(this)
        text = "Course Average: 0.0"
    }
    override fun invalidated(observable: Observable?) {
        text = "Course Average: " + Model.getbelow1().toString()
    }
}
// View of course taken
class Viewtaken: Label(), InvalidationListener {
    init {
        Model.addListener(this)
        text = "Courses Taken: 0"
    }
    override fun invalidated(observable: Observable?) {
        text = "Courses Taken: " + Model.getbelow2().toString()
    }
}
// View of course failed
class Viewfailed: Label(), InvalidationListener {
    init {
        Model.addListener(this)
        text = "Courses Failed: 0"
    }
    override fun invalidated(observable: Observable?) {
        text = "Courses Failed: " + Model.getbelow3().toString()
    }
}
class ViewWD: Label(), InvalidationListener {
    init {
        Model.addListener(this)
        text = "Courses WD'ed: 0"
    }
    override fun invalidated(observable: Observable?) {
        text = "Courses WD'ed: " + Model.getbelow4().toString()
    }
}
class AvaTerm: Canvas(550.0, 450.0), InvalidationListener {
    init {
        Model.addListener(this)
    }

    override fun invalidated(observable: Observable?) {
        v1.graphicsContext2D.apply {
            val w = Model.root.width - 350.0
            val h = Model.root.height
            val map = mutableMapOf<String, Double>()
            if (term_list.size != 0) {
                // x value
                val xList = fillxvalue(this, term_list, map, w, h)

                // points
                xList.forEach {
                    val ava = termAva(mylist, it)
                    val xValue = map[it]
                    val yValue = 0.8 * h - (0.71 * h * (ava / 100)) - 0.01 * h
                    if (xValue != null) {
                        if (ava >= 0.0) {
                            when (ava.toInt()) {
                                in 0..49 -> { fill = Color.LIGHTCORAL }
                                in 50..59 -> { fill = Color.LIGHTBLUE }
                                in 60..90 -> { fill = Color.LIGHTGREEN }
                                in 91..95 -> { fill = Color.SILVER }
                                in 96..100 -> { fill = Color.GOLD }
                            }
                            fillOval(xValue, yValue, 10.0, 10.0)
                        }
                    }
                }
                // add lines
                var i = 0
                var next = 1
                xList.forEach { _ ->
                    if (i <= (xList.size - 2)) {
                        var write = false

                        val ava1 = termAva(mylist, xList[i])
                        val x1 = map[xList[i]]
                        val y1 = 0.8 * h - (0.71 * h * (ava1 / 100)) - 0.01 * h
                        if (x1 != null && ava1 >= 0.0) {
                            // find next
                            while(!write) {
                                val ava2 = termAva(mylist, xList[i+next])
                                val x2 = map[xList[i+next]]
                                val y2 = 0.8 * h - (0.71 * h * (ava2 / 100)) - 0.01 * h
                                if (x2 != null && ava2 >= 0.0) {
                                    strokeLine(x1 + 5.0, y1 + 5.0, x2 + 5.0, y2 + 5.0)
                                    write = true
                                    next = 1
                                }
                                if (ava2 == -1.0){
                                    next++
                                }
                            }
                        }
                    }
                    i++
                }
            }
        }
    }
}
class PtDegree: Canvas(550.0, 450.0), InvalidationListener {
    init {
        Model.addListener(this)
    }
    override fun invalidated(observable: Observable?) {
        v2.graphicsContext2D.apply {
            val w = Model.root.width - 350.0
            val h = Model.root.height
            val csCount = count(mylist, "CS")
            val mathCount = count(mylist, "MATH") + count(mylist, "CO") + count(mylist, "STAT")
            val otherCount = mylist.size - csCount - mathCount
            initRect()

            // rectangle
            fill = Color.YELLOW
            var taken = csCount * 19.8 / (2 * 550)
            fillRect(0.17 * w, 0.17 * h - 17.5, taken * w, 35.0)
            fill = Color.RED
            taken = mathCount * 19.8 / (2 * 550)
            fillRect(0.17 * w, 0.17 * h + 0.18 * h - 17.5, taken * w, 35.0)
            fill = Color.GRAY
            taken = otherCount * 19.8 / (2 * 550)
            fillRect(0.17 * w, 0.17 * h + 0.36 * h - 17.5, taken * w, 35.0)
            fill = Color.GREEN
            taken = mylist.size * 19.8 / (2 * 550)
            fillRect(0.17 * w, 0.17 * h + 0.54 * h - 17.5, taken * w, 35.0)


        }
    }
}
class COutcome: Canvas(550.0, 400.0), InvalidationListener {
    init {
        Model.addListener(this)
    }
    override fun invalidated(observable: Observable?) {
        canvas3.graphicsContext2D.apply {
            val w = Model.root.width - 350.0
            val h = Model.root.height - 70.0
            val gardeList = gList(mylist)
            var sdegree = 0.0
            var total = mylist.size

            if (mc.isSelected) {
                total = 40
            }

            // draw arc
            val legendY = h - 50.0
            legend(this, 20.0, legendY)
            if (total != 0 && mylist.size != 0) {
                (0..5).forEach {
                    val degree = gardeList[it] * (360.0 / total)
                    when (it) {
                        0 -> {
                            fill = Color.DARKSLATEGRAY
                            fillArc(0.223 * w, 0.07 * h, 0.56 * w, 0.56 * w, sdegree, degree, ArcType.ROUND)
                            sdegree += degree
                        }
                        1 -> {
                            fill = Color.LIGHTCORAL
                            fillArc(0.223 * w, 0.07 * h, 0.56 * w, 0.56 * w, sdegree, degree, ArcType.ROUND)
                            sdegree += degree
                        }

                        2 -> {
                            fill = Color.LIGHTBLUE
                            fillArc(0.223 * w, 0.07 * h, 0.56 * w, 0.56 * w, sdegree, degree, ArcType.ROUND)
                            sdegree += degree
                        }

                        3 -> {
                            fill = Color.LIGHTGREEN
                            fillArc(0.223 * w, 0.07 * h, 0.56 * w, 0.56 * w, sdegree, degree, ArcType.ROUND)
                            sdegree += degree
                        }

                        4 -> {
                            fill = Color.SILVER
                            fillArc(0.223 * w, 0.07 * h, 0.56 * w, 0.56 * w, sdegree, degree, ArcType.ROUND)
                            sdegree += degree
                        }

                        5 -> {
                            fill = Color.GOLD
                            fillArc(0.223 * w, 0.07 * h, 0.56 * w, 0.56 * w, sdegree, degree, ArcType.ROUND)
                        }
                    }
                }
            }

            // legend

        }
    }
}
class IncreAva: Canvas(550.0, 400.0), InvalidationListener {
    init {
        Model.addListener(this)
    }
    override fun invalidated(observable: Observable?) {
        v4.graphicsContext2D.apply {
            val w = Model.root.width - 350.0
            val h = Model.root.height
            val map = mutableMapOf<String, Double>()
            if (term_list.size != 0) {
                // x value
                val xList = fillxvalue(this, term_list, map, w, h)

                // points
                var i = 0
                val gradeList = mutableListOf<Double>()
                xList.forEach {me ->
                    val list = findList(mylist, me)
                    list.forEach { gradeList.add(it) }
                    val ava = findAva(gradeList)
                    gradeList.forEach {
                        val value = it
                        val xValue = map[xList[i]]
                        var yValue = 0.8 * h - (0.71 * h * (value / 100)) - 0.01 * h
                        lineWidth = 2.0
                        if (xValue != null) {
                            if (value >= 0.0) {
                                when (value.toInt()) {
                                    in 0..49 -> { stroke = Color.LIGHTCORAL }
                                    in 50..59 -> { stroke = Color.LIGHTBLUE }
                                    in 60..90 -> { stroke = Color.LIGHTGREEN }
                                    in 91..95 -> { stroke = Color.SILVER }
                                    in 96..100 -> { stroke = Color.GOLD }
                                }
                                strokeOval(xValue, yValue, 10.0, 10.0)
                            }
                        }
                        // sd
                        val sd = findSD(gradeList)
                        if (gradeList.size >= 2) {
                            if (xValue != null) {
                                stroke = Color.BLACK
                                lineWidth = 1.0
                                yValue = 0.8 * h - (0.71 * h * (ava / 100)) - 0.01 * h
                                strokeOval(xValue, yValue, 10.0, 10.0)
                                fill = Color.LIGHTGREEN
                                fillOval(xValue, yValue, 10.0, 10.0)
                                stroke = Color.BLACK
                                var y2 = 0.8 * h - (0.71 * h * ((ava + sd) / 100)) - 0.01 * h
                                strokeLine(xValue + 5.0, yValue, xValue + 5.0, y2)
                                strokeLine(xValue, y2, xValue + 10.0, y2)
                                y2 = 0.8 * h - (0.71 * h * ((ava - sd) / 100)) - 0.01 * h
                                strokeLine(xValue + 5.0, yValue + 10.0, xValue + 5.0, y2)
                                strokeLine(xValue, y2, xValue + 10.0, y2)
                            }
                        }
                    }
                    i++
                }
            }
        }
    }
}


// top
val top_toolbar = ToolBar(top_name, top_term, top_grade, ControllerCreate()).apply{
    background = Background(BackgroundFill(Color.LIGHTGRAY, CornerRadii(7.0), Insets(3.0)))
    maxWidth = Double.MAX_VALUE
}

// centre
val center_part = View()
val v1 = AvaTerm()
val v2 = PtDegree()
val canvas3 = COutcome().apply {
    addEventHandler(MouseEvent.MOUSE_MOVED) { it: MouseEvent ->
        val centrex = width / 2
        val centrey = height / 2
        val x = it.x - centrex
        val y = it.y - centrey
        val r = width / 4
        val dis = sqrt(x * x + y * y)

        if (dis <= r) {
            val pixelReader = this.snapshot(null, null).pixelReader
            val bgName = when (pixelReader.getColor(it.x.toInt(), it.y.toInt())) {
                Color.DARKSLATEGRAY -> "WD"
                Color.LIGHTCORAL -> "49"
                Color.LIGHTBLUE -> "59"
                Color.LIGHTGREEN -> "90"
                Color.SILVER -> "95"
                Color.GOLD -> "100"
                else -> ""
            }
            drawtext(this.graphicsContext2D, bgName)
        } else {
            drawtext(this.graphicsContext2D, "")
        }
    }
}
val mc = ControllerMC()
val v3 = AnchorPane().apply {
    children.add(0, canvas3)
    children.add(1, mc.apply {
        AnchorPane.setBottomAnchor(this, 15.0)
        AnchorPane.setLeftAnchor(this, 225.0)
    })
}
val v4 = IncreAva()


val visualization = TabPane().apply{
    tabs.add(Tab("Average by Term", v1).apply { isClosable = false })
    tabs.add(Tab("Progress towards Degree", v2).apply { isClosable = false })
    tabs.add(Tab("Course Outcomes", v3).apply { isClosable = false })
    tabs.add(Tab("Incremental Averages", v4).apply { isClosable = false })
    prefWidth = 550.0
    maxWidth = Double.MAX_VALUE
    HBox.setHgrow(this, Priority.ALWAYS)
    VBox.setVgrow(this, Priority.ALWAYS)
}


// bottom
val bottom_part = ToolBar(Viewava(), Separator(), Viewtaken(), Separator(), Viewfailed(),
    Separator(), ViewWD())

// Stage
class HelloApplication : Application() {
    override fun start(stage: Stage) {
        Model.root.apply {
            widthProperty().addListener { _, _, newWidth ->
                v1.width = newWidth as Double
                v2.width = newWidth
                canvas3.width = newWidth - 350.0
                v4.width = newWidth
                Model.visual1()
                Model.visual2()
                Model.visual3()
                Model.visual4()
            }
            heightProperty().addListener { _, _, newHeight ->
                v1.height = newHeight as Double
                v2.height = newHeight
                canvas3.height = newHeight - 70.0
                v4.height = newHeight
                Model.visual1()
                Model.visual2()
                Model.visual3()
                Model.visual4()
            }
        }

        stage.apply {
            scene = Scene(Model.root, 900.0, 450.0)
            title = "My Mark Visualization"
        }.show()
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}