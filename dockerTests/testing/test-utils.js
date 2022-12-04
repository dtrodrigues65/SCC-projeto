'use strict';

/***
 * Exported functions to be used in the testing scripts.
 */
module.exports = {
  uploadImageBody,
  processUploadReply,
  genNewUser,
  genNewUserReply,
  delUserReply,
  updateUser,
  updateUserReply,
  selectUserSkewed,
  genNewAuction,
  genNewBid,
  genNewQuestion,
  genNewQuestionReply,
  decideToReply,
}


const Faker = require('faker')
const fs = require('fs');

var imagesIds = []
var images = []
var users = []

// All endpoints starting with the following prefixes will be aggregated in the same for the statistics
var statsPrefix = [ ["/rest/media/","GET"],
			["/rest/media","POST"],
			["/rest/user/","GET"],
	]

// Function used to compress statistics
global.myProcessEndpoint = function( str, method) {
	var i = 0;
	for( i = 0; i < statsPrefix.length; i++) {
		if( str.startsWith( statsPrefix[i][0]) && method == statsPrefix[i][1])
			return method + ":" + statsPrefix[i][0];
	}
	return method + ":" + str;
}

// Auxiliary function to select an element from an array
Array.prototype.sample = function(){
	   return this[Math.floor(Math.random()*this.length)]
}

Array.prototype.sampleSkewed = function(){
	return this[randomSkewed(this.length)]
}

// Returns a random value, from 0 to val
function random( val){
	return Math.floor(Math.random() * val)
}

function randomSkewed( val){
	let beta = Math.pow(Math.sin(Math.random()*Math.PI/2),2)
	let beta_left = (beta < 0.5) ? 2*beta : 2*(1-beta);
	return Math.floor(beta_left * val)
}

// Loads data about images from disk
function loadData() {
	var i
	var basefile
	if( fs.existsSync( '/images')) 
		basefile = '/images/cats.'
	else
		basefile =  'images/cats.'	
	for( i = 1; i <= 40 ; i++) {
		var img  = fs.readFileSync(basefile + i + '.jpeg')
		images.push( img)
	}
	var str;
	if( fs.existsSync('users.data')) {
		str = fs.readFileSync('users.data','utf8')
		users = JSON.parse(str)
	} 
}

loadData();

/**
 * Sets the body to an image, when using images.
 */
function uploadImageBody(requestParams, context, ee, next) {
	requestParams.body = images.sample()
	return next()
}

/**
 * Process reply of the download of an image. 
 * Update the next image to read.
 */
function processUploadReply(requestParams, response, context, ee, next) {
	if( typeof response.body !== 'undefined' && response.body.length > 0) {
		imagesIds.push(response.body)
	}
    return next()
}

/**
 * Select an image to download.
 */
function selectImageToDownload(context, events, done) {
	if( imagesIds.length > 0) {
		context.vars.imageId = imagesIds.sample()
	} else {
		delete context.vars.imageId
	}
	return done()
}

/**
 * Select an image to download.
 */
function selectUser(context, events, done) {
	if( userIds.length > 0) {
		context.vars.userId = userIds.sample()
	} else {
		delete context.vars.userId
	}
	return done()
}

/**
 * Generate data for a new user using Faker
 */
function genNewUser(context, events, done) {
	const first = `${Faker.name.firstName()}`
	const last = `${Faker.name.lastName()}`
	context.vars.id = first + "." + last
	context.vars.name = first + " " + last
	context.vars.pwd = `${Faker.internet.password()}`
	return done()
}


/**
 * Process reply for of new users to store the id on file
 */
function genNewUserReply(requestParams, response, context, ee, next) {
	if( response.statusCode >= 200 && response.statusCode < 300 && response.body.length > 0)  {
		let u = JSON.parse( response.body)
		users.push(u)
		fs.writeFileSync('users.data', JSON.stringify(users));
	}
    return next()
}

function delUserReply(requestParams, response, context, ee, next){
	if( response.statusCode >= 200 && response.statusCode < 300 && response.body.length > 0)  { 
		let u = JSON.parse(response.body)
		u = findUser(u.id)
		let index = users.indexOf(u)		
		users.splice(index,1)
		fs.writeFileSync('users.data', JSON.stringify(users));
			
	}
	return next()	
}


function findUser( id){
	for( var u of users) {
		if( u.id === id)
			return u;
	}
	return null
}


function updateUser(context,events,done) {
	const first = `${Faker.name.firstName()}`
	const last = `${Faker.name.lastName()}`
	let u = findUser(context.vars.user)
	context.vars.name = first + " " + last
	context.vars.imageId = u.photoId
	return done()
}

function updateUserReply(requestParams, response, context, ee, next) {
	if( response.statusCode >= 200 && response.statusCode < 300 && response.body.length > 0)  {
		let u0 = findUser(context.vars.id)
		let index = users.indexOf(u0)		
		users.splice(index, 1)
		let u = JSON.parse( response.body)
		users.push(u)
		fs.writeFileSync('users.data', JSON.stringify(users));
	}
    return next()
}

function selectUserSkewed(context, events, done) {
	if( users.length > 0) {
		let user = users.sampleSkewed()
		context.vars.user = user.id
		context.vars.pwd = user.pwd
	} else {
		delete context.vars.user
		delete context.vars.pwd
	}
	return done()
}

function genNewAuction(context, events, done) {
	context.vars.title = `${Faker.commerce.productName()}`
	context.vars.description = `${Faker.commerce.productDescription()}`
	context.vars.minimumPrice = `${Faker.commerce.price(1,999)}`
	var maxBids = 5
	if( typeof context.vars.maxBids !== 'undefined')
		maxBids = context.vars.maxBids;
	var maxQuestions = 2
	if( typeof context.vars.maxQuestions !== 'undefined')
		maxQuestions = context.vars.maxQuestions;
	var d = new Date();
	d.setTime(Date.now() + random( 300000));
	context.vars.endTime = d.toISOString();
	context.vars.status = "OPEN";
	context.vars.numBids = random( maxBids);
	context.vars.numQuestions = random( maxQuestions);
	return done()
}

/**
 * Generate data for a new bid
 */
 function genNewBid(context, events, done) {
	if( typeof context.vars.bidValue == 'undefined') {
		if( typeof context.vars.minPrice == 'undefined') {
			context.vars.bidValue = 1000
		} else {
			context.vars.bidValue = context.vars.minimumPrice + 10
		}
	}
	context.vars.value = context.vars.bidValue +10;
	context.vars.bidValue = context.vars.bidValue + 10
	return done()
}

/**
 * Generate data for a new question
 */
 function genNewQuestion(context, events, done) {
	context.vars.text = `${Faker.lorem.paragraph()}`;
	return done()
}

/**
 * Generate data for a new reply
 */
 function genNewQuestionReply(context, events, done) {
	var user = findUser( context.vars.auctionUser);
	context.vars.auctionUserPwd = user.pwd;
	context.vars.reply = `${Faker.lorem.paragraph()}`;
	return done()
}

/**
 * Decide whether to reply
 */
 function decideToReply(context, events, done) {
	if (context.vars.questionOne.length == 0) {
		delete context.vars.questionId
	} else {
		context.vars.questionId = context.vars.questionOne.sample().id
	}
	context.vars.reply = `${Faker.lorem.paragraph()}`;
	return done()
}


  

