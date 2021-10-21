import { IPostsForm } from '@service'

export * from './NormalEditForm'
export * from './QnAEditForm'

export interface EditFormProps {
  //   handleSave: () => void
  post: IPostsForm
}
