// file ID server

// imports

var fs = require('fs');
var http = require('http');
var https = require('https');
var express = require('express');
var mysql = require('mysql');

var options = {key: fs.readFileSync('credentials/key.pem', 'utf8'), cert: fs.readFileSync('credentials/cert.pem', 'utf8'), passphrase: 'bassword'};
var app = express();
var router = express.Router();
var remoteIP = '';

// restful API setup

// setup mysql
var connection = mysql.createConnection({
	host:'127.0.0.1',
	user:'webapp',
	password:'bassword',
	database:'bitwav'
});

connection.connect(function(err) {
	if (err) {
		console.error('error connecting: ' + err.stack);
		return;
	}

	console.log('connected as id ' + connection.threadId);
});

// setup routing

app.use(function(req, res, next) {
	var fip = req.socket.remoteAddress.split(':');
	console.log('Incoming connection from ' + fip[fip.length - 1]);
	
	next();
});

router.get('/files/:id', function(req, res) {
	res.download('assets/' + req.params.id);
});

router.get('/fileinfo/:name', function(req, res) {
	connection.query('select * from fileSummaries where name = \'' + req.params.name + '\'', function(error, result) {
		if (error) {
			throw error;
		}

		res.json(result);
	});
});

router.get('/fileinfo/:name/filesize', function(req, res) {
	connection.query('select filesize from fileSummaries where name = \'' + req.params.name + '\'', function(error, result) {
		if (error) {
			res.send('No file with that name exist.');
			throw error;
		}

		res.json(result)
	});
});

router.get('/ips/:id', function(req, res) {
	connection.query('select ip from activeips where id = \'' + req.params.file + '\'', function(error, result) {
		if (error) {
			throw error;
		}

		res.json(result);
	});
});

router.post('/ips/:id', function(req, res) {
	var fip =  req.socket.remoteAddress.split(':');
	connection.query('insert into activeips (ip, id) values (\'' + fip[fip.length - 1] + '\', \'' + req.params.id + '\')', function(error, result) {
		if (error) {
			throw error;
		}
		console.log(req.params.id);
		res.json(result);
	});
});

router.delete('/ips/:ip', function(req, res) {
	connection.query('delete from  activeips where ip=\'' + req.params.ip + '\'', function(error, result) {
		if (error) {
			throw error;
		}

		res.json(result);
	});
});

app.use('/', router);

var httpServer = http.createServer(app);
var httpsServer = https.createServer(options, app);

httpServer.listen(8008);
httpsServer.listen(8443);
