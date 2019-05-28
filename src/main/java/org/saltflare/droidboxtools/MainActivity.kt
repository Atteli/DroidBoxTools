package org.saltflare.droidboxtools

import android.app.PendingIntent.getActivity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.DataOutputStream
import java.io.File
import java.lang.Compiler.command
import java.util.Collections.list
import android.view.View.OnFocusChangeListener
import java.util.Collections.emptyList


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /*
        ACTUAL APP
         */

        val su = Runtime.getRuntime().exec("su")
        val outputStream = DataOutputStream(su.outputStream)


        val ledList: MutableList<String> = getLeds()
        //val ledList: MutableList<String> = mutableListOf("/yo/lo1:number1", "/yo/lo2:number2", "/yo/lo3:number3")
        val ledNames: MutableList<String> = getLedNames(ledList)
        var ledOptions: MutableList<String> = mutableListOf()

        var actualLed = ""
        var actualOption = ""

        //of /sys/devices/platform/leds/leds/<LED>:<MODE> only use <LED> for displaying in dropdown menu

        //ledNames.forEach{ item -> Log.i("Test", item)}

        //creating blink option adapter for showing led blink options in dropdown menu
        val led_mode_adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ledOptions)
        led_mode_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ledOptionSpinner.adapter = led_mode_adapter
        ledOptionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                outputStream.writeBytes("echo " + selectedItem + " > " + actualLed)
                outputStream.flush();
                su.waitFor();
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        //creating led adapter for showing leds in dropdown menu
        val led_adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ledNames)
        led_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ledSpinner.adapter = led_adapter
        ledSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //val selectedItem = parent.getItemAtPosition(position).toString()
                led_mode_adapter.clear()
                ledOptions = getLedOptionNames(ledList[position])
                val selectedOption = ledOptions.find { item -> item.contains("[") }
                Log.i("selected", selectedOption)
                led_mode_adapter.addAll(ledOptions)
                ledOptionSpinner.setSelection(led_mode_adapter.getPosition(selectedOption))
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        Log.i("lel", ledSpinner.selectedItemId.toString() + " " + ledSpinner.selectedItem.toString() + " " + ledSpinner.selectedItemPosition.toString() )







        Log.i("MainActivity","finished")




        // led stuff
        //val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, getLeds())
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //ledspinner.adapter = adapter
    }

    private fun getLeds(): MutableList<String> {
        val ledList: MutableList<String> = mutableListOf()

        val folders = File("/sys/devices/platform/leds/leds/").listFiles {
                file -> file.isDirectory()
        }
        folders.forEach {
                folder -> ledList.add(folder.toString())
        }
        //ledList.forEach { Log.i("lol", it) }
        return ledList.toMutableList()
    }

    private fun getLedNames(ledList: MutableList<String>): MutableList<String> {
        val ledNames: MutableList<String> = mutableListOf()
        ledList.forEach { item -> ledNames.add(item.substring(item.lastIndexOf("/") + 1).split(":")[0]) }
        return ledNames
    }

    private fun getLedOptionNames(ledName: String): MutableList<String> {
        val triggerFile = ledName + "/trigger"
        //triggerFile.replace(":","\\:")
        Log.i("triggerfile", triggerFile)
        val optionList: List<String> = File(triggerFile).readText(Charsets.UTF_8).trim().split(" ")
        optionList.forEach{ Log.i("trf", it)}

        Log.i("triggerfile_size", optionList.size.toString())
        return optionList.toMutableList()
    }
}
