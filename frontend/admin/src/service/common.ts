import axios from 'axios'

export interface IResponse {
  err?: {
    status: number
    message: string
  }
}

export interface IReqeust {
  url: string
}

const headers = {
  'Content-Type': 'application/json',
}

export interface SearchPayload {
  keywordType?: string
  keyword?: string
  size?: number
  page?: number
}

//목록 데이터 조회하는 fetcher
const fetcher = async (url: string, param: {}) => {
  const res = await axios.get(url, {
    params: param,
    headers,
  })

  return res.data
}

export const common = {
  headers,
  fetcher,
}
