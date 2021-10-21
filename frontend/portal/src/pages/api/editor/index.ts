import { CUSTOM_HEADER_SITE_ID_KEY, EDITOR_LOAD_IMAGE_URL } from '@constants'
import { SERVER_API_URL, SITE_ID } from '@constants/env'
import axios from 'axios'
import multer from 'multer'
import { NextApiRequest, NextApiResponse } from 'next'

const upload = multer({
  storage: multer.memoryStorage(),
})

const initMiddleware = (middleware: any) => {
  return (req: NextApiRequest, res: NextApiResponse) =>
    new Promise((resolve, reject) => {
      middleware(req, res, result => {
        if (result instanceof Error) {
          return reject(result)
        }
        return resolve(result)
      })
    })
}

// for parsing multipart/form-data
// editor 요청인 경우 무조건 single임
const multerAny = initMiddleware(upload.single('upload'))

type NextApiRequestWithFormData = NextApiRequest & {
  file: Express.Multer.File
}

export const config = {
  api: {
    bodyParser: false,
  },
}

export default async (
  req: NextApiRequestWithFormData,
  res: NextApiResponse,
) => {
  await multerAny(req, res)

  //첨부파일 base64 endoding -> 서버에서 decoding 필요
  if (req.file.size > 300000) {
    res.status(501).json({ message: 'File is too big!! 😵‍💫' })
    return
  }

  const base64Encoding = req.file.buffer.toString('base64')

  const body = {
    fieldName: req.file.fieldname,
    originalName: req.file.originalname,
    fileType: req.file.mimetype,
    size: req.file.size,
    fileBase64: base64Encoding,
  }

  //headers
  let editorHeaders = {
    'Content-Type': 'application/json',
  }
  editorHeaders[CUSTOM_HEADER_SITE_ID_KEY] = SITE_ID

  // const cookies = new Cookies(req, res)
  // const authToken = cookies.get(ACCESS_TOKEN)
  // //  header에 authentication 추가
  // if (authToken) {
  //   editorHeaders[CLAIM_NAME] = authToken
  // }

  const result = await axios.post(
    `${SERVER_API_URL}/portal-service/api/v1/upload/editor`,
    body,
    {
      headers: editorHeaders,
    },
  )

  let data = {}
  if (result) {
    data = {
      ...result.data,
      url: `${SERVER_API_URL}${EDITOR_LOAD_IMAGE_URL}${result.data.url}`,
    }
  }

  res.status(200).json(data)
}
