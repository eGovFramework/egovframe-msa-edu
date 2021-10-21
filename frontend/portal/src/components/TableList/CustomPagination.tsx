import Button from '@material-ui/core/Button'
import IconButton from '@material-ui/core/IconButton'
import FirstPageIcon from '@material-ui/icons/FirstPage'
import KeyboardArrowLeft from '@material-ui/icons/KeyboardArrowLeft'
import KeyboardArrowRight from '@material-ui/icons/KeyboardArrowRight'
import LastPageIcon from '@material-ui/icons/LastPage'
import React from 'react'
interface CustomPaginationProps {
  page: number
  totalPages: number
  first: boolean
  last: boolean
  onChangePage: (
    event: React.MouseEvent<HTMLButtonElement>,
    newPage: number,
  ) => void
}

export default function CustomPagination(props: CustomPaginationProps) {
  const { page, totalPages, first, last, onChangePage } = props

  const handleFirstPageButtonClick = (
    event: React.MouseEvent<HTMLButtonElement>,
  ) => {
    onChangePage(event, 0)
  }

  const handleBackButtonClick = (
    event: React.MouseEvent<HTMLButtonElement>,
  ) => {
    onChangePage(event, page - 1)
  }

  const handleNextButtonClick = (
    event: React.MouseEvent<HTMLButtonElement>,
  ) => {
    onChangePage(event, page + 1)
  }

  const handleLastPageButtonClick = (
    event: React.MouseEvent<HTMLButtonElement>,
  ) => {
    onChangePage(event, totalPages - 1)
  }
  const handlePageButtonClick = (
    event: React.MouseEvent<HTMLButtonElement>,
    page: number,
  ) => {
    onChangePage(event, page)
  }

  return (
    <div className="paging">
      <nav className="MuiPagination-root">
        <div className="MuiPagination-ul">
          <IconButton
            className="MuiPaginationItem-root MuiPaginationItem-page"
            onClick={handleFirstPageButtonClick}
            disabled={first}
            aria-label="first page"
          >
            <FirstPageIcon />
          </IconButton>
          <IconButton
            className="MuiPaginationItem-root MuiPaginationItem-page"
            onClick={handleBackButtonClick}
            disabled={first}
            aria-label="previous page"
          >
            <KeyboardArrowLeft />
          </IconButton>
          {totalPages > 0
            ? [...Array(totalPages).keys()].map(item => (
                <Button
                  className={`MuiPaginationItem-root MuiPaginationItem-page ${
                    page === item ? 'Mui-selected' : ''
                  }`}
                  key={`pagin-item-${item}`}
                  onClick={e => {
                    handlePageButtonClick(e, item)
                  }}
                >
                  {item + 1}
                </Button>
              ))
            : null}
          <IconButton
            className="MuiPaginationItem-root MuiPaginationItem-page"
            onClick={handleNextButtonClick}
            disabled={last}
            aria-label="next page"
          >
            <KeyboardArrowRight />
          </IconButton>
          <IconButton
            className="MuiPaginationItem-root MuiPaginationItem-page"
            onClick={handleLastPageButtonClick}
            disabled={last}
            aria-label="last page"
          >
            <LastPageIcon />
          </IconButton>
        </div>
      </nav>
    </div>
  )
}
