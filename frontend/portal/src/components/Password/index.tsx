import { Control, FormState } from 'react-hook-form'

export * from './PasswordChange'
export * from './PasswordConfirm'
export * from './PasswordDone'

export interface IUserPasswordForm {
  currentPassword: string
  newPassword: string
  newPasswordConfirm: string
}

export interface PasswordProps {
  control: Control<any>
  formState: FormState<any>
  handleList: () => void
}
