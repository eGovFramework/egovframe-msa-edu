import { DEFAULT_ERROR_MESSAGE } from '@constants'
import { AxiosError } from 'axios'
import { atom, DefaultValue, selector } from 'recoil'

/**
 * Global error 상태관리
 */

interface IErrors {
  defaultMessage: string
  field: string
  rejectedValue?: string
  message?: string
}

export interface IErrorProps {
  open?: boolean
  error?: AxiosError
  status?: number
  message?: string
  errors?: IErrors[]
}

export const errorStateAtom = atom<IErrorProps>({
  key: 'errorStateAtom',
  default: { error: null } as IErrorProps,
})

export const errorStateSelector = selector<IErrorProps>({
  key: 'errorStateSelector',
  get: ({ get }) => {
    return get(errorStateAtom)
  },
  set: ({ set, reset }, newValue) => {
    if (newValue instanceof DefaultValue) {
      reset(errorStateAtom)
    } else {
      const error = newValue.error
      let message = error?.message || DEFAULT_ERROR_MESSAGE
      let errors: IErrors[] = []
      let status = 500
      if (error?.response) {
        message = error.response.data.message || message

        errors = error.response.data.errors
        status = error.response.status
      }

      set(errorStateAtom, {
        open: true,
        error,
        status,
        message,
        errors,
      })
    }
  },
})
