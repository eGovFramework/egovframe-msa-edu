import { ACCESS_TOKEN, AUTH_USER_ID, CLAIM_NAME } from '@constants/env'
import axios from 'axios'

const JWT_EXPIRED_TIME = 1800000
const LOGIN_SERVICE_URL = `/api/login/user-service`
const LOGIN_URL = `/login`
const CLIENT_REFRESH_URL = '/api/login/user-service/api/v1/users/token/refresh'

export interface ILogin {
  email?: string
  password?: string
  isRemember?: boolean
  provider: 'email' | 'google' | 'naver' | 'kakao'
  token?: string
  name?: string
}

const onSuccessLogin = (result: any) => {
  axios.defaults.headers.common[CLAIM_NAME] = result[ACCESS_TOKEN]
  axios.defaults.headers.common[AUTH_USER_ID] = result[AUTH_USER_ID]
  // access-token 만료 1분 전에 로그인 연장
  setTimeout(loginSerivce.silentRefresh, JWT_EXPIRED_TIME - 60000)
}

export const loginSerivce = {
  login: (data: ILogin) => {
    return new Promise<string>(async (resolve, reject) => {
      try {
        const result = await fetch(LOGIN_SERVICE_URL + LOGIN_URL, {
          method: 'POST',
          body: JSON.stringify(data),
        })

        if (result.status === 200) {
          onSuccessLogin(await result.json())
          resolve('success')
        } if (result.status === 412) {
          reject('join')
        } else {
          reject('noAuth')
        }
      } catch (error) {
        reject(error)
      }
    })
  },
  silentRefresh: async () => {
    try {
      const result = await fetch(CLIENT_REFRESH_URL, {
        method: 'PUT',
      })
      if (result) {
        onSuccessLogin(await result.json())
      }
    } catch (error) {
      console.warn('refresh token 만료로 인한 로그아웃!!!!')
      fetch('/api/v1/token')
        .then(res => {
          console.warn('fetch', res)
        })
        .catch(error => {
          console.warn('fetch error', error)
        })
    }
  },
}
