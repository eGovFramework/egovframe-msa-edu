import React from 'react'
import Collapse from '@material-ui/core/Collapse'
import IconButton from '@material-ui/core/IconButton'
import TableCell from '@material-ui/core/TableCell'
import TableRow from '@material-ui/core/TableRow'
import KeyboardArrowDownIcon from '@material-ui/icons/KeyboardArrowDown'
import KeyboardArrowUpIcon from '@material-ui/icons/KeyboardArrowUp'
import {
  GridRowData,
  GridRowId,
  GridValueFormatterParams,
} from '@material-ui/data-grid'
import { CollapseColDef } from './CollapsibleTable'

interface CollapseRowProps {
  columns: CollapseColDef[]
  row: GridRowData
  collapseColumn: React.ReactNode
  collapseClassName?: string
  rowId?: GridRowId
}

const formatterParams = (
  column: CollapseColDef,
  row: GridRowData,
  rowId: GridRowId,
): GridValueFormatterParams => {
  return {
    id: row[rowId],
    field: column.field,
    value: row[column.field],
    row: row,
    colDef: { ...column, computedWidth: column.width },
    api: null,
    cellMode: column.editable ? 'edit' : 'view',
    hasFocus: false,
    tabIndex: -1,
    getValue: (id: GridRowId, field: string) => row[field],
  }
}

const cellParams = (
  formatParams: GridValueFormatterParams,
  column: CollapseColDef,
  row: GridRowData,
) => {
  let formattedValue = formatParams.value
  if (column.valueFormatter) {
    formattedValue = column.valueFormatter(formatParams)
  }

  return {
    ...formatParams,
    formattedValue,
  }
}

const renderCell = (
  column: CollapseColDef,
  row: GridRowData,
  rowId: GridRowId,
  rowIdx: number,
  toggleOpen: () => void,
) => {
  let cell: React.ReactNode = <>{row[column.field]}</>

  const gridValueFormatterParams = formatterParams(column, row, rowId)
  if (column.valueFormatter) {
    cell = column.valueFormatter(gridValueFormatterParams)
  } else if (column.renderCell) {
    cell = column.renderCell(cellParams(gridValueFormatterParams, column, row))
  }

  return (
    <TableCell
      key={`collapse-cell-${column.field}-${rowIdx}`}
      align={column.align}
      width={column.width}
      className={String(column.cellClassName) || ''}
      onClick={toggleOpen}
    >
      {cell}
    </TableCell>
  )
}

const CollapseRow = (props: CollapseRowProps) => {
  const {
    columns,
    row,
    collapseColumn,
    collapseClassName = 'content',
    rowId = 'id',
  } = props
  const [open, setOpen] = React.useState(false)

  const toggleOpen = () => {
    setOpen(!open)
  }

  return (
    <>
      <TableRow style={{ height: 70 }}>
        {columns.map((col, index) => {
          return renderCell(col, row, rowId, index, toggleOpen)
        })}

        <TableCell align="center">
          <IconButton
            aria-label="expand row"
            size="small"
            onClick={() => setOpen(!open)}
          >
            {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          </IconButton>
        </TableCell>
      </TableRow>
      <TableRow>
        <TableCell
          style={{ padding: 0, width: '50px' }}
          colSpan={columns.length + 1}
        >
          <Collapse in={open} timeout="auto" unmountOnExit>
            <div className={collapseClassName}>{collapseColumn}</div>
          </Collapse>
        </TableCell>
      </TableRow>
    </>
  )
}

export default CollapseRow
