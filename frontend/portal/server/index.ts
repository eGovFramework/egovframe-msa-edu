import { loadEnvConfig } from '@next/env'
import express, { Request, Response } from 'express'
import next from 'next'
loadEnvConfig('./', process.env.NODE_ENV !== 'production')

const port = process.env.PORT || 3000
const dev = process.env.NODE_ENV !== 'production'
const app = next({ dev })
const handle = app.getRequestHandler()

const startServer = async () => {
  try {
    await app.prepare()
    const server = express()

    server.all('*', (req: Request, res: Response) => {
      return handle(req, res)
    })

    server.listen(port, (err?: any) => {
      if (err) throw err
      console.log(`> Ready on localhost:${port} - env ${process.env.NODE_ENV}`)
    })
  } catch (error) {
    console.error(error)
    process.exit(1)
  }
}

startServer()
