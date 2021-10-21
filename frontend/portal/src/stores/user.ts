import { atom } from 'recoil'

export interface IUser {
  email: string
  userId: string
  userName: string
  googleId?: string
  kakaoId?: string
  naverId?: string
  isSocialUser: boolean
  hasPassword: boolean
  verification?: any
}

export const userAtom = atom<IUser>({
  key: 'userAtom',
  default: null,
})
