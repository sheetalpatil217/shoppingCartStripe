'use strict';


var mongoose = require('mongoose'),
Login = mongoose.model('login'),
Users = mongoose.model('user'),
bodyParser = require('body-parser');


const jwt= require('jsonwebtoken');

exports.create_a_user = function(req, res) {
  console.log("Reached Inside Signup");
  if (!req.body.email || !req.body.password) {
      res.status(400).send({
      message: "Invalid parameters"
    });
    }
  console.log("Reached checking params");

    var email_req=req.body.email;
    var password_req=req.body.password;
    Login.find({
          _id : email_req
      },function(error,comments){
        console.log("Error================",error);
       console.log("========comments==========", comments);

       if (error) {
         res.status(400).send({
           message: "Error occured while fetching from user schema"
            });
               }
        //   console.log("Reached Inside Signup");
        if(comments.length==0){
          var first_name_req=req.body.first_name;
          var last_name_req=req.body.last_name;
          var loginUser={
            "_id":email_req,
            "password":password_req,
          }
          var login_task=new Login(loginUser);
          login_task.save(function(err,loginUser){
            if (err){
             // res.send(err);
            }
          });
          var userObj= {
            _id:email_req,
            first_name:first_name_req,
            last_name:last_name_req
            }
            var users_task=new Users(userObj);
            users_task.save(function(err,userObj){
                if(err){
                // res.send(err);
                }
                res.send("success");
            });

}
else{
    res.send("User exists");
}
});
};


exports.signin_user = function(req, res) {

  var email_req=req.body.email;
  var passwowrd=req.body.password;
  Login.find({_id : email_req}, function(error, comments) {
       const response = {};
       var tok=0;
       if(comments.length){
         if(comments[0].password === req.body.password){
           jwt.sign({email:email_req},"secretkey",(err,token) =>{
             response.status = "Success";
             response.message = "User Sucessfully logged In";
             response.token=token;
             console.log(response);
             res.send(response);          });

         }else {
           response.status = "Failure";
           response.message = "password not correct";
           res.send(response);
         }
       }else{
         response.status = "Failure";
         response.message = "user not found";
         res.send(response)
       }
   });
};


exports.fetch_user_profile = function verifytoken(req,res) {
console.log("inside fetch user profile");
console.log("got token back from the android",req.token);

console.log("req header",req.headers);
console.log("req header",req.headers['authorization']);

  const bearerHeader=req.headers['authorization'];

  if(typeof bearerHeader !=="undefined"){
    const bearer = bearerHeader.split(" ");
    const bearer_token= bearer[1];
    req.token = bearer_token;
  //  req.token= bearerHeader;
    console.log("inside bearer");

    console.log(req.token)
    jwt.verify(req.token, 'secretkey',(err,authData)=>{
      if(err){
        res.send(403);
        console.log("forbidden from validate");
      }else{
//  res.send("validated");
            getuserProfile(req.query.email,req,res)
        //console.log(data);

      }
    });
  }else{
    res.sendStatus(403);
  }
}

function getuserProfile(email,req,res){
  Users.find({_id : email}, function(error, comments) {
      var response = {};
      console.log("inside find",);
       if(comments.length){
         console.log("User Found");
         console.log(comments[0]);
        var user=comments[0];
        response.status=200;
        response.data=user;
   }else{
    response.status=502;
    response.message="User Not found";
  }
    res.send(response);
   });
}




exports.update_user_profile = function(req, res) {

  console.log("update user profile");

  console.log("inside validate function");

  const bearerHeader=req.headers['Authorization'];

  if(typeof bearerHeader !=="undefined"){
    const bearer = bearerHeader.split(" ");
    const bearer_token= bearer[1];
    req.token = bearer_token;
    console.log("inside bearer");

    jwt.verify(req.token, 'secretkey',(err,authData)=>{
      if(err){
        res.send(403);
        console.log("forbidden from validate");
      }else{
//  res.send("validated");
            console.log(req.body);
            updateuserProfile(req.body.user,req,res)
        //console.log(data);

      }
    });
  }else{
    res.sendStatus(403);
  }
};


function updateuserProfile(user,req,res){
  console.log(user["email"]);
  console.log(user.email);
  Users.findOneAndUpdate({_id : user.email},{
    first_name:user.first_name,
    last_name:user.last_name,
    age:user.age,
    weight:user.weight,
    address:user.address
    } ,function(error, comments) {
  //     var response = {};
  //      if(comments.length){
  //        console.log("User Found");
  //        console.log(comments[0]);
  //       var user=comments[0];
  //       response.status=200;
  //       response.data=user;
  //  }else{
  //   response.status=502;
  //   response.message="User Not found";
  // }
  //   res.send(response);
  if(error){
    var response={};
    response.status=402;
    response.message="Not  Updated";
    res.send(response);
  }
  var response={};
  response.status=200;
  response.message="User Updated Successfully";
  res.send(response);

   });
}
