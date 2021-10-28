import axios from 'axios'

/**
 * 사용자 서비스 사용자 API URL
 */
const USER_URL = '/user-service/api/v1/users'

export interface IUser {
  email: string
  password?: string
  userName: string
  token?: string
}

// 비밀번호 찾기로 변경
export interface IUserFindPassword {
  emailAddr?: string
  userName?: string
  password?: string
  tokenValue?: string
}

// 사용자 검증
export interface IVerification {
  provider: string
  password?: string
  token?: string
}

// 마이페이지 비밀번호 변경
interface IUserUpdatePassword extends IVerification {
  newPassword: string
}

// 마이페이지 회원정보 변경
interface IUserUpdate extends IVerification {
  email: string
  userName: string
}

// 소셜 정보
export interface ISocialUser {
  id: string
  email: string
  name: string
}

/**
 * 사용자 관리 서비스
 */
export const userService = {
  social: (provider: string, token: string) =>
    axios.post(`${USER_URL}/social`, { provider, token }),
  existsEmail: (email: string, userId?: string) =>
    new Promise<boolean>((resolve, rejects) => {
      axios
        // .get(`${USER_URL}/exists?email=${email}`)
        .post(`${USER_URL}/exists`, { email, userId })
        .then(result => {
          resolve(result.data)
        })
        .catch(e => {
          rejects(e)
        })
    }),
  join: (user: IUser) =>
    new Promise<boolean>((resolve, rejects) => {
      axios
        .post(`${USER_URL}/join`, user)
        .then(result => {
          resolve(result.data)
        })
        .catch(e => {
          rejects(e)
        })
    }),
  findPassword: (userFindPassword: IUserFindPassword) =>
    new Promise<boolean>((resolve, rejects) => {
      axios
        .post(`${USER_URL}/password/find`, userFindPassword)
        .then(result => {
          resolve(result.data)
        })
        .catch(e => {
          rejects(e)
        })
    }),
  getFindPassword: (token: string) => {
    return axios.get(`${USER_URL}/password/valid/${token}`)
  },
  changePassword: (userFindPassword: IUserFindPassword) =>
    new Promise<boolean>((resolve, rejects) => {
      axios
        .put(`${USER_URL}/password/change`, userFindPassword)
        .then(result => {
          resolve(result.data)
        })
        .catch(e => {
          rejects(e)
        })
    }),
  updatePassword: (userUpdatePassword: IUserUpdatePassword) =>
    new Promise<boolean>((resolve, rejects) => {
      axios
        .put(`${USER_URL}/password/update`, userUpdatePassword)
        .then(result => {
          resolve(result.data)
        })
        .catch(e => {
          rejects(e)
        })
    }),
  matchPassword: (password: string) =>
    new Promise<boolean>((resolve, rejects) => {
      axios
        .post(`${USER_URL}/password/match`, { password })
        .then(result => {
          resolve(result.data)
        })
        .catch(e => {
          rejects(e)
        })
    }),
  updateInfo: (userId: string, userUpdate: IUserUpdate) =>
    new Promise<boolean>((resolve, rejects) => {
      axios
        .put(`${USER_URL}/info/${userId}`, userUpdate)
        .then(result => {
          resolve(result.data)
        })
        .catch(e => {
          rejects(e)
        })
    }),
  leave: (userLeave: IVerification) =>
    new Promise<boolean>((resolve, rejects) => {
      axios
        .post(`${USER_URL}/leave`, userLeave)
        .then(result => {
          resolve(result.data)
        })
        .catch(e => {
          rejects(e)
        })
    }),
}
