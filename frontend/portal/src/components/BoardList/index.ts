import { CollapseColDef } from '@components/TableList'
import { GridColDef } from '@material-ui/data-grid'
import { Page } from '@service'
import { TFunction } from 'next-i18next'

export * from './NormalBoardList'
export * from './FAQBoardList'
export * from './QnABoardList'

export type ColumnsType = (
  data: Page,
  t?: TFunction,
) => GridColDef[] | CollapseColDef[]
