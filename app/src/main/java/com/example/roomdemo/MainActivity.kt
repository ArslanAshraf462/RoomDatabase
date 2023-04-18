package com.example.roomdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdemo.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        var employeeDAO = (application as EmployeeApp).db.employeeDao()
        binding?.btnAdd?.setOnClickListener {
            addRecord(employeeDAO)
        }
        lifecycleScope.launch {
            employeeDAO.fetchAllEmployee().collect{
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list,employeeDAO)
            }
        }
    }

    private fun addRecord(employeeDAO: EmployeeDAO){
        val name = binding?.etName?.text.toString()
        val email = binding?.etEmailId?.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty()){
           lifecycleScope.launch {
               employeeDAO.insert(EmployeeEntity(name = name, email = email))
               Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_SHORT).show()
               binding?.etName?.text?.clear()
               binding?.etEmailId?.text?.clear()
           }
        }else{
            Toast.makeText(applicationContext, "Name or Email cannot be blank", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setupListOfDataIntoRecyclerView(
        employeesList: ArrayList<EmployeeEntity>,
        employeeDAO: EmployeeDAO
    ){
        if(employeesList.isNotEmpty()){
            val itemAdapter = ItemAdapter(employeesList,
                )

            binding?.rvItemsList?.layoutManager = LinearLayoutManager(this)
            binding?.rvItemsList?.adapter = itemAdapter
            binding?.rvItemsList?.visibility = View.VISIBLE
            binding?.tvNoRecordsAvailable?.visibility = View.GONE
        }else{
            binding?.rvItemsList?.visibility = View.GONE
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
        }
    }
}