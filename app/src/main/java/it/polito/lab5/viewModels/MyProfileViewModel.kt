package it.polito.lab5.viewModels

import androidx.lifecycle.ViewModel
import it.polito.lab5.model.MyModel

class MyProfileViewModel(val model:MyModel) : ViewModel(){
    val users = model.users
}