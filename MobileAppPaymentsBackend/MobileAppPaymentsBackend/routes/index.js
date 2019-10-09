'use strict';

var express = require('express');
const stripe = require('stripe')('sk_test_374C2HvwanWhkWCRDDmxVhn800PJ5LfAGJ');
//var braintree = require('braintree');
var router = express.Router(); // eslint-disable-line new-cap
var gateway = require('../lib/gateway');
var Task = require('../models/userProfileAPIModels');
var mongoose = require('mongoose');
var Login = mongoose.model('login');
var Users = mongoose.model('user');
var products = mongoose.model('products');
//var stripe_key = stripe('pk_test_oU7GbhcQQXVU6JkcPyWGPmTK00TpFq4dEb');

//const token = request.body.stripeToken;

const jwt = require('jsonwebtoken');

router.post("/charge", (req, res) => {

    var amt = Math.round(req.body.amount.toFixed(2) * 100);
    var token = req.body.stripeToken;
    var customer_id = req.body.customer_ID;

    console.log("Insider the charge");
    console.log("amt", amt);
    console.log("token", token);

    console.log(req.headers);

    const bearerHeader = req.headers['authorization'];

    if (typeof bearerHeader !== "undefined") {
        const bearer = bearerHeader.split(" ");
        const bearer_token = bearer[1];
        req.token = bearer_token;
        console.log("inside bearer");

        jwt.verify(req.token, 'secretkey', (err, authData) => {
            if (err) {
                res.send(403);
                console.log("forbidden from validate");
            } else {

                stripe.customers.createSource(
                    customer_id, {
                        source: token,
                    },
                    function (err, card) {
                       
                        if (err) {
                            console.log(err);
                           
                        }
                        if (card) {
                            console.log(card);
                            console.log("card is called");
                            stripe.charges.create({
                                customer: customer_id,
                                amount: amt,
                                currency: 'usd',
                                description: 'Total cost of the items',
                                statement_descriptor: 'Custom',

                            }, function (err,result) {
                                 var response={};
                                if(err){
                                     console.log(err);
                                     response.status="failure";
                                }
                                response.status="success";
                                response.data=result;
                                console.log("response",result);
                                res.send(response);
                               
                            });
                        }
                    });

            }
        });
    } else {
        console.log("Failed here");
        res.sendStatus(403);
    }
});


router.post('/chargePrevious', function (req, res) {
    console.log("charge previous");
    var amt = Math.round(req.body.amount * 100);
    var customer_id = req.body.customer_ID;
    var card_id = req.body.card_id;

    console.log("Insider the charge previous");
    console.log("amt", amt);
    console.log("customer_id", amt);
    console.log("card_id", card_id);

    console.log(req.headers);

    const bearerHeader = req.headers['authorization'];

    if (typeof bearerHeader !== "undefined") {
        const bearer = bearerHeader.split(" ");
        const bearer_token = bearer[1];
        req.token = bearer_token;
        console.log("inside bearer");

        jwt.verify(req.token, 'secretkey', (err, authData) => {
            if (err) {
                res.send(403);
                console.log("forbidden from validate");
            } else {

                stripe.charges.create({
                    customer: customer_id,
                    source: card_id,
                    amount: amt,
                    currency: 'usd',
                    description: 'Total cost of the items',
                    statement_descriptor: 'Custom',
                }, function (err, result) {
                      var response = {};
                        if(err){
                              console.log(err);
                            response.status="failure";
                        }
                        response.status="success";
                        response.data=result.id;
                    res.send(response);
                });

                console.log("Inside the Charges creation");
            }
        });
    } else {
        console.log("Failed here");
        res.sendStatus(403);
    }

});


router.post('/getCards', function (req, res) {
    var customer_id = req.body.customer_ID;
    const bearerHeader = req.headers['authorization'];

    if (typeof bearerHeader !== "undefined") {
        const bearer = bearerHeader.split(" ");
        const bearer_token = bearer[1];
        req.token = bearer_token;
        console.log("inside bearer");

        jwt.verify(req.token, 'secretkey', (err, authData) => {
            if (err) {
                res.send(403);
                console.log("forbidden from validate");
            } else {
                console.log("inside else cards");
                stripe.customers.listSources(
                    customer_id, {
                        limit: 3,
                        object: 'card',
                    },
                    function (err, cards) {
                            var response = {};
                        console.log("inside cards function");
                        // asynchronously called
                        if (err) {
                            console.log(err);
                            response.status="failure";
                        }
                        response.status="success";
                        response.data=cards;
                        console.log(response);
                        console.log(response.data);
                        res.send(response);
                    });
            }
        });
    } else {
        console.log("Failed here");
        res.sendStatus(403);
    }

});



router.get('/', function (req, res) {
    res.redirect('/checkouts/new');
});

//This function will create token for client before payent

router.get('/checkouts/new', function (req, res) {

    console.log("inside validate function");

    console.log(req.headers);

    const bearerHeader = req.headers['authorization'];

    if (typeof bearerHeader !== "undefined") {
        const bearer = bearerHeader.split(" ");
        const bearer_token = bearer[1];
        req.token = bearer_token;
        console.log("inside bearer");

        jwt.verify(req.token, 'secretkey', (err, authData) => {
            if (err) {
                res.send(403);
                console.log("forbidden from validate");
            } else {
                gateway.clientToken.generate({
                    customerId: authData.braintree_id
                }, function (err, response) {
                    // res.render('checkouts/new', {clientToken: response.clientToken, messages: req.flash('error')});
                    console.log("hit the checkouts new");
                    res.send(JSON.stringify(response)); // Instead of render a view, we just return an json object for the API
                });
            }
        });
    } else {
        console.log("Failed here");
        res.sendStatus(403);
    }
});

router.get('/checkouts/:id', function (req, res) {
    var result;
    var transactionId = req.params.id;

    gateway.transaction.find(transactionId, function (err, transaction) {
        result = createResultObject(transaction);
        res.render('checkouts/show', {
            transaction: transaction,
            result: result
        });
    });
});

// This function wil process checkout
router.post('/checkouts', function (req, res) {
    var transactionErrors;
    var amount = req.body.amount; // In production you should not take amounts directly from clients
    var nonce = req.body.payment_method_nonce;

    console.log("amount in gateway", amount);

    const bearerHeader = req.headers['authorization'];
    console.log("bearerHeader: " + bearerHeader);

    if (typeof bearerHeader !== "undefined") {
        const bearer = bearerHeader.split(" ");
        const bearer_token = bearer[1];
        req.token = bearer_token;
        console.log("Inside Bearer");

        jwt.verify(req.token, 'secretkey', (err, authData) => {
            if (err) {
                res.send(403);
                console.log("forbidden from validate");
            } else {
                gateway.transaction.sale({
                    amount: amount,
                    paymentMethodNonce: nonce,
                    options: {
                        submitForSettlement: true
                    }
                }, function (err, result) {
                    if (result.success || result.transaction) {
                        // res.redirect('checkouts/' + result.transaction.id);
                        res.send(result);
                    } else {
                        console.log("error in checkouts", err);
                        transactionErrors = result.errors.deepErrors();
                        //  req.flash('error', {msg: formatErrors(transactionErrors)});
                        // res.redirect('checkouts/new');
                        res.send(formatErrors(transactionErrors));
                    }
                });
            }
        })

    } else {
        console.log("Bearer header undefined in else");
        res.send("Bearer not correct");
    }
});

router.post('/signup', function (req, res) {
    console.log("Reached Inside Signup");
    if (!req.body.email || !req.body.password) {
        res.status(400).send({
            message: "Invalid parameters"
        });
    }
    console.log("Reached checking params");

    var email_req = req.body.email;
    var password_req = req.body.password;
    Login.find({
        _id: email_req
    }, function (error, comments) {
        console.log("Error================", error);
        console.log("========comments==========", comments);

        if (error) {
            res.status(400).send({
                message: "Error occured while fetching from user schema"
            });
        }
        //   console.log("Reached Inside Signup");
        if (comments.length == 0) {
            var first_name_req = req.body.first_name;
            var last_name_req = req.body.last_name;
            var loginUser = {
                "_id": email_req,
                "password": password_req,
            }
            var login_task = new Login(loginUser);
            login_task.save(function (err, loginUser) {
                if (err) {
                    // res.send(err);
                }
            });

            stripe.customers.create({
                name: first_name_req,
                email: email_req,
                //source: stripe.createToken()
            }, function (err, customer) {
                if (err) {
                    console.log(err);
                }
                customer.success;

                // true
                console.log("success");
                console.log(customer);

                console.log("created customer id", customer.id);

                var userObj = {
                    _id: email_req,
                    first_name: first_name_req,
                    last_name: last_name_req,
                    stripe_id: customer.id
                }
                console.log(userObj);

                var users_task = new Users(userObj);
                users_task.save(function (err, userObj) {
                    if (err) {
                        //res.send(err);
                    }
                    res.send("success");
                    console.log("user object created successfully");
                });

            });

        } else {
            res.send("User exists");
        }



    });

});

router.post('/signin', function (req, res) {
    var email_req = req.body.email;
    var passwowrd = req.body.password;
    Login.find({
        _id: email_req
    }, function (error, comments) {
        const response = {};
        var tok = 0;
        // var user_brain_id;
        if (comments.length) {
            if (comments[0].password === req.body.password) {
                Users.find({
                    _id: email_req
                }, function (error, comments) {
                    var response = {};
                    console.log("inside find", );
                    if (comments.length) {
                        console.log("User Found");
                        console.log(comments[0]);
                        var user = comments[0];
                        //var user_brain_id = user.braintree_id;
                        var first_name = user.firstName;
                        console.log(user.first_name);
                        //console.log(user_brain_id);
                        jwt.sign({
                            email: email_req,
                            stripe_id: user.stripe_id
                        }, "secretkey", (err, token) => {
                            response.status = "Success";
                            response.message = "User Sucessfully logged In";
                            response.token = token;
                            response.first_name = user.first_name;
                            response.customer_id = user.stripe_id;
                            console.log(response);
                            res.send(response);
                        });

                    }
                });
            } else {
                response.status = "Failure";
                response.message = "password not correct";
                res.send(response);
            }
        } else {
            response.status = "Failure";
            response.message = "user not found";
            res.send(response)
        }
    });
});

router.get('/userprofile/', function (req, res) {
    console.log("inside fetch user profile");
    console.log("got token back from the android", req.token);

    console.log("req header", req.headers);
    console.log("req header", req.headers['authorization']);

    const bearerHeader = req.headers['authorization'];

    if (typeof bearerHeader !== "undefined") {
        const bearer = bearerHeader.split(" ");
        const bearer_token = bearer[1];
        req.token = bearer_token;
        //  req.token= bearerHeader;
        console.log("inside bearer");

        console.log(req.token)
        jwt.verify(req.token, 'secretkey', (err, authData) => {
            if (err) {
                res.send(403);
                console.log("forbidden from validate");
            } else {
                //  res.send("validated");
                console.log(authData);
                console.log(authData.email);
                getuserProfile(authData.email, req, res)
                //console.log(data);

            }
        });
    } else {
        res.sendStatus(403);
    }
});

router.put('/userprofile/', function (req, res) {
    console.log("update user profile");

    console.log("inside validate function");

    const bearerHeader = req.headers['authorization'];

    if (typeof bearerHeader !== "undefined") {
        const bearer = bearerHeader.split(" ");
        const bearer_token = bearer[1];
        req.token = bearer_token;
        console.log("inside bearer");

        jwt.verify(req.token, 'secretkey', (err, authData) => {
            if (err) {
                res.send(403);
                console.log("forbidden from validate");
            } else {
                //  res.send("validated");
                console.log(req.body);
                updateuserProfile(authData.email, req, res)
                //console.log(data);

            }
        });
    } else {
        res.sendStatus(403);
    }
});



function getuserProfile(email, req, res) {
    Users.find({
        _id: email
    }, function (error, comments) {
        var response = {};
        console.log("inside find", );
        if (comments.length) {
            console.log("User Found");
            console.log(comments[0]);
            var user = comments[0];
            response.status = 200;
            response.data = user;
        } else {
            response.status = 502;
            response.message = "User Not found";
        }
        res.send(response);
    });
}



function updateuserProfile(user, req, res) {
    console.log(user["email"]);
    console.log(user.email);
    Users.findOneAndUpdate({
        _id: user.email
    }, {
        first_name: user.first_name,
        last_name: user.last_name,
        age: user.age,
        weight: user.weight,
        address: user.address
    }, function (error, comments) {
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
        if (error) {
            var response = {};
            response.status = 402;
            response.message = "Not  Updated";
            res.send(response);
        }
        var response = {};
        response.status = 200;
        response.message = "User Updated Successfully";
        res.send(response);

    });
}


router.get('/products', function (req, res) {
    var response = {};
    //var productList={};
    const bearerHeader = req.headers['authorization'];
    console.log("bearerHeader: " + bearerHeader);

    if (typeof bearerHeader !== "undefined") {
        const bearer = bearerHeader.split(" ");
        const bearer_token = bearer[1];
        req.token = bearer_token;
        console.log("Inside Bearer");

        jwt.verify(req.token, 'secretkey', (err, authData) => {
            if (err) {
                res.send(403);
                console.log("forbidden from validate");
            } else {
                products.find({}, function (err, items) {
                    //console.log(items);
                    // productList.push(items);

                    if (items.length > 0) {
                        response.status = 200;
                        response.message = "products sent successfully";
                        response.data = items;
                        res.send(response);

                    } else {
                        response.status = 402;
                        response.message = "product list empty";
                        res.send(response);
                    }
                });
            }
        })
    } else {
        console.log("Failed here");
        res.sendStatus(403);
    }
});


module.exports = router