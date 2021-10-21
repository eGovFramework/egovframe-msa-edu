import React from 'react'
import {
  DataGrid,
  DataGridProps,
  GridColDef,
} from '@material-ui/data-grid'
import withWidth, {
  WithWidthProps,
} from '@material-ui/core/withWidth'
import { DEFUALT_GRID_PAGE_SIZE } from '@constants'
import DataGridPagination from './DataGridPagination'

interface DataGridTableProps extends WithWidthProps, DataGridProps {
  xsColumns: GridColDef[]
}

const DataGridTable = (props: DataGridTableProps) => {
  const { columns, rows, xsColumns, width, getRowId, pageSize, ...rest } = props

  return (
    <div className="list">
      <DataGrid
        rows={rows || []}
        columns={width === 'xs' ? xsColumns : columns}
        pageSize={pageSize || DEFUALT_GRID_PAGE_SIZE}
        disableSelectionOnClick
        disableColumnFilter
        disableColumnMenu
        disableDensitySelector
        headerHeight={width === 'xs' ? 0 : 70}
        rowHeight={width === 'xs' ? 80 : 70}
        autoHeight
        pagination
        components={{ Pagination: DataGridPagination }}
        getRowId={getRowId || (r => r.id)}
        {...rest}
      />
    </div>
  )
}

export default withWidth()(DataGridTable)
