'use strict';

var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var session = require('express-session');
var flash = require('connect-flash');
var fs = require('fs');

var routes = require('./routes/index');
var mongoose = require('mongoose');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'pug');

app.use(favicon(path.join(__dirname, 'public/images', 'favicon.png')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));
app.use(cookieParser());
app.use(session({
  // this string is not an appropriate value for a production environment
  // read the express-session documentation for details
  secret: '---',
  saveUninitialized: true,
  resave: true
}));
app.use(express.static(path.join(__dirname, 'public')));
app.use(flash());
app.use('/models',express.static('models'));

app.use('/', routes);






mongoose.Promise = global.Promise;
mongoose.connect('mongodb://localhost/UserProfiledb');
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function callback() {
  console.log("Sucessfully connected to Db");
});



// fs.readFile('./supportFiles/discount.json', 'utf8', function (err, data) {
//     if (err) throw err;
//    var json = JSON.parse(data);
//   //  console.log(json.results);
// db.on('open', function () {
//     db.db.listCollections().toArray(function (err, collectionNames) {
//         if (err) {
//             console.log(err);
//             return;
//         }
//         console.log(collectionNames);
//      /*   collectionNames.forEach(collectionNames,function(item){
//             console.log(collectionNames[item].name);
//         })*/
//           db.db.createCollection("products", function (err, res) {
//                 if (err) throw err;
//                 console.log("Collection created!");
//                 // db.close();
//             });
//
//          db.db.collection("products").insertMany(json.results, function (err, res) {
//         if (err) throw err;
//         console.log("Number of documents inserted: " + res.insertedCount);
//         // db.close();
//     });});
//
//     });
//
//
// });

// catch 404 and forward to error handler
app.use(function (req, res, next) {
  var err = new Error('Not Found');

  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function (err, req, res, next) { // eslint-disable-line no-unused-vars
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function (err, req, res, next) { // eslint-disable-line no-unused-vars
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});

module.exports = app;
