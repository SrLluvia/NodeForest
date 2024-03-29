package com.nodeforest.activity

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.nodeforest.impl.MinHeapImpl
import com.nodeforest.R
import de.blox.treeview.BaseTreeAdapter
import de.blox.treeview.TreeNode
import de.blox.treeview.TreeView
import kotlin.math.log2
import kotlin.math.pow

class MinHeapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minheap)

        val treeView = findViewById<TreeView>(R.id.idTreeViewMain)

        //Adaptador necesario para el treeview
        val adapter: BaseTreeAdapter<ViewHolder?> = object : BaseTreeAdapter<ViewHolder?>(this,
            R.layout.tree_view_node
        ) {
            override fun onCreateViewHolder(view: View?): ViewHolder {
                return ViewHolder(view)
            }

            override fun onBindViewHolder(viewHolder: ViewHolder?, data: Any?, position: Int) {
                viewHolder!!.textView.setText(data.toString())
            }
        }

        //Enlazamos el adaptador con el treeview
        treeView.setAdapter(adapter)

        val heap = MinHeapImpl<Int>()

        val btnDelete: Button = findViewById(R.id.btnDelete)
        btnDelete.isEnabled = false
        btnDelete.setOnClickListener{
            deleteValue(heap, adapter, btnDelete, treeView)
        }

        val btnAdd: Button = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener{
            addValue(heap, adapter, btnDelete, treeView)
        }

    }

    /*
        Se llama cuando se pulsa el boton de añadir.
        Muestra un dialogo para poder introducir un valor al heap, que se añadira en caso de ser
        un valor valido (0-99), o se rechazara en caso de no serlo.
     */
    private fun addValue(heap: MinHeapImpl<Int>, adapter: BaseTreeAdapter<ViewHolder?>,
                         btnDelete: Button, treeView: TreeView) {
        val addDialog = AlertDialog.Builder(this)
        addDialog.setTitle("Añade un elemento:")
        addDialog.setMessage("Elemento a añadir:")

        val inputNumber = EditText(this)
        inputNumber.inputType = InputType.TYPE_CLASS_NUMBER
        addDialog.setView(inputNumber)

        addDialog.setPositiveButton("Ok", DialogInterface.OnClickListener(){
                dialog, i ->
            val inputNumberValue = Integer.parseInt(inputNumber.text.toString())
            if(inputNumberValue in 0..99){
                heap.insert(inputNumberValue)
                val treeArray = arrayToTreeArray(heap.getArray())
                showTree(treeArray,adapter, treeView)
                btnDelete.isEnabled = true
            }else{
                Toast.makeText(this, "El valor debe estar entre 0 y 99", Toast.LENGTH_SHORT).show()
            }

        })
        addDialog.setNegativeButton("Cancelar", DialogInterface.OnClickListener(){
                dialog, i ->
        })
        addDialog.create()
        addDialog.show()
    }

    /*
        Se llama cuando se pulsa el boton de eliminar.
        Elimina el valor raiz del heap.
     */
    private fun deleteValue(heap: MinHeapImpl<Int>, adapter: BaseTreeAdapter<ViewHolder?>,
                            btnDelete: Button, treeView: TreeView) {
        heap.removeMinValue()
        val treeArray = arrayToTreeArray(heap.getArray())
        if(treeArray.size > 0) {
            showTree(treeArray, adapter, treeView)
        }else{
            showTree(null, adapter, treeView)
            btnDelete.isEnabled = false
        }
    }

    /*
        Representa el arbol correspondiente dado un array
     */
    private fun showTree(result: ArrayList<TreeNode?>?, adapter: BaseTreeAdapter<ViewHolder?>,
                         treeView: TreeView){
        if(result != null) {
            treeView.visibility = View.VISIBLE
            adapter.setRootNode(result[0]!!)
            var index = 0
            while (index < result.size / 2) {
                if (result[2 * index + 1] == null && result[2 * index + 2] == null) {
                    index++
                    continue
                }

                //Hijo izquierdo
                if (result[2 * index + 1] != null) {
                    result[index]!!.addChild(result[2 * index + 1])
                } else {
                    //Nulo
                    result[index]!!.addChild(TreeNode("N"))
                }

                //Hijo derecho
                if (result[2 * index + 2] != null) {
                    result[index]!!.addChild(result[2 * index + 2])
                } else {
                    //Nulo
                    result[index]!!.addChild(TreeNode("N"))
                }
                index++
            }
        }else{
            treeView.visibility = View.INVISIBLE
        }
    }

    /*
        Transforma el array del heap en un array de TreeNode
     */
    private fun arrayToTreeArray(array: ArrayList<Int?>): ArrayList<TreeNode?> {
        val result = ArrayList<TreeNode?>()
        for(value in array){
            result.add(TreeNode(value))
        }
        val size = result.size
        //Altura del heap
        val height = log2(size.toDouble()).toInt() +1
        //Tamaño para que este completo
        val maxSize = (2.0.pow(height)-1).toInt()

        //Nulos que faltan para tener un arbol completo
        val toComplete = maxSize - size
        for(i in 0 until toComplete){
            result.add(null)
        }

        return result
    }
}