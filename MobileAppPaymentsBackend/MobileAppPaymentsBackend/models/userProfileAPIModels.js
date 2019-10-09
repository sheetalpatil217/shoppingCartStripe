'use strict';
var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var LoginSchema = new Schema({

  _id: {
    type:String,
    required:true,
    unique:true,
    // validate(value){
    //   if(!validator.isEmail(value)){                        // npm install validator
    //     throw new Error('Email is invalid')
    //   }
    // }
  },
  password:{
     type:String,
     required:true,
     minlength:7
   }
});

var UserProfileSchema = new Schema({

  _id: {
    type:String,
    required:true,
    unique:true,
    // validate(value){
    //   if(!validator.isEmail(value)){                        // npm install validator
    //     throw new Error('Email is invalid')
    //   }
    // }
  },
  first_name: {
    type: String,
    required: true
  },
  last_name: {
     type: String,
     required: true
   },
  age: {
    type: Number,
    default:0,
    // validate(value){
    //   if(value < 0){
    //     throw new Error('Age must be a positive number')
    //   }
    // }
  },
  weight: {
    type: Number,
    default: 0
  },
  address: {
    type: String,
    default:""
  },
     stripe_id:{
           type:String,
     required:true,
     unique:true,
     }
});

var login = mongoose.model('login', LoginSchema);
var user = mongoose.model('user',UserProfileSchema);
const dataSchema = new mongoose.Schema({});
const products = mongoose.model('products', dataSchema, 'products');

module.exports = login;
module.exports = user;
module.exports=products;
