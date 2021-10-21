import React from 'react'
import Pagination, { PaginationProps } from '@material-ui/lab/Pagination'
import { useGridSlotComponentProps } from '@material-ui/data-grid'
import PaginationItem from '@material-ui/lab/PaginationItem'

interface DataGridPaginationProps extends PaginationProps {}

const DataGridPagination = (props: DataGridPaginationProps) => {
  const { state, apiRef } = useGridSlotComponentProps()

  return (
    <div className="paging">
      <Pagination
        color="primary"
        page={state.pagination.page + 1}
        count={state.pagination.pageCount}
        showFirstButton={true}
        showLastButton={true}
        // @ts-expect-error
        renderItem={item => <PaginationItem {...item} disableRipple />}
        onChange={(event, value) => apiRef.current.setPage(value - 1)}
        {...props}
      />
    </div>
  )
}

export default DataGridPagination
