import express from 'express'
import serveStatic from 'serve-static'
import parser from 'body-parser'

import routes from './routes'

const app = express()
const port = process.env.PORT || 3000

/*
* middleware
**/
// app.use(morgan('dev'));
app.use(parser.json())
app.use(parser.urlencoded({ extended: true }))

/*
* router
**/
app.use(routes)

/*
* server front end static assets
**/
app.use(serveStatic(__dirname))

/*
* expose port
**/
app.listen(port)
console.log('server started '+ port)
