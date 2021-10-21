import {
  GRID_PAGE_SIZE,
  GRID_ROWS_PER_PAGE_OPTION,
  GRID_ROW_HEIGHT,
} from '@constants'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import { DataGrid, DataGridProps } from '@material-ui/data-grid'
import * as React from 'react'
import DataGridPagination from './DataGridPagination'

export interface IDataGridProps extends DataGridProps {}

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      '& .hover': {
        cursor: 'pointer',
        color: '#1a3e72',
      },
    },
  }),
)

export default function CustomDataGrid(props: IDataGridProps) {
  const { columns, rows, pageSize, rowsPerPageOptions, rowHeight, getRowId } =
    props
  const classes = useStyles()
  return (
    <div className={classes.root}>
      <DataGrid
        {...props}
        rows={rows || []}
        columns={columns}
        rowHeight={rowHeight || GRID_ROW_HEIGHT}
        pageSize={pageSize || GRID_PAGE_SIZE}
        rowsPerPageOptions={rowsPerPageOptions || GRID_ROWS_PER_PAGE_OPTION}
        autoHeight
        pagination
        components={{ Pagination: DataGridPagination }}
        getRowId={getRowId || (r => r.id)}
      />
    </div>
  )
}
