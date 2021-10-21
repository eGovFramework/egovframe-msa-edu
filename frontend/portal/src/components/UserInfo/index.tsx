import { Control, FormState } from 'react-hook-form'

export * from './UserInfoDone'
export * from './UserInfoModified'
export interface UserInfoProps {
  control: Control<any>
  formState: FormState<any>
  handleList: () => void
}

export interface IUserForm {
  currentPassword?: string
  password: string
  email: string
  userName: string
}
