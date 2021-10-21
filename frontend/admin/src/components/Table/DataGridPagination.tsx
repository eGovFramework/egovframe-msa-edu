import React from 'react'
import { useGridSlotComponentProps } from '@material-ui/data-grid'
import Pagination, { PaginationProps } from '@material-ui/lab/Pagination'
import PaginationItem from '@material-ui/lab/PaginationItem'

export default function DataGridPagination(props: PaginationProps) {
  const { state, apiRef } = useGridSlotComponentProps()

  return (
    <Pagination
      color="primary"
      variant="outlined"
      shape="rounded"
      page={state.pagination.page + 1}
      count={state.pagination.pageCount}
      showFirstButton={true}
      showLastButton={true}
      // @ts-expect-error
      renderItem={item => <PaginationItem {...item} disableRipple />}
      onChange={(event, value) => apiRef.current.setPage(value - 1)}
      {...props}
    />
  )
}
