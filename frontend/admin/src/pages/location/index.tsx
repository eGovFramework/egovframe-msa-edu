import { GridButtons } from '@components/Buttons'
import Search from '@components/Search'
import CustomDataGrid from '@components/Table/CustomDataGrid'
import { GRID_PAGE_SIZE } from '@constants'
import usePage from '@hooks/usePage'
import useSearchTypes from '@hooks/useSearchType'
import { convertStringToDateFormat } from '@libs/date'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import {
  GridCellParams,
  GridColDef,
  GridValueFormatterParams,
  GridValueGetterParams,
} from '@material-ui/data-grid'
import { locationService } from '@service'
import { conditionAtom, errorStateSelector } from '@stores'
import { Page, rownum } from '@utils'
import { useRouter } from 'next/router'
import React, { useCallback, useMemo } from 'react'
import { TFunction, useTranslation } from 'react-i18next'
import { useRecoilValue, useSetRecoilState } from 'recoil'
// material-ui style
const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
      '& .MuiOutlinedInput-input': {
        padding: theme.spacing(1),
      },
    },
  }),
)
const conditionKey = 'location'

type ColumnType = (
  data: Page,
  handleDelete: (id: number) => void,
  handleUpdate: (id: number) => void,
  toggleIsUse: (event: React.ChangeEvent<HTMLInputElement>, id: number) => void,
  t: TFunction,
) => GridColDef[]

//그리드 컬럼 정의
const getColumns: ColumnType = (
  data: Page,
  handleDelete: (id: number) => void,
  handleUpdate: (id: number) => void,
  toggleIsUse: (event: React.ChangeEvent<HTMLInputElement>, id: number) => void,
  t,
) => {
  return [
    {
      field: 'rownum',
      headerName: t('common.no'),
      headerAlign: 'center',
      align: 'center',
      width: 80,
      sortable: false,
      valueGetter: (params: GridValueGetterParams) =>
        rownum(data, params.api.getRowIndex(params.id), 'asc'),
    },
    {
      field: 'locationName',
      headerName: t('location.name'),
      headerAlign: 'center',
      align: 'left',
      flex: 1,
      sortable: false,
    },
    {
      field: 'isUse',
      headerName: t('common.use_at'),
      headerAlign: 'center',
      align: 'center',
      width: 120,
      sortable: false,
      renderCell: (params: GridCellParams) => (
        <Switch
          checked={Boolean(params.value)}
          onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
            toggleIsUse(event, params.row.locationId)
          }
        />
      ),
    },
    {
      field: 'createDate',
      headerName: t('common.created_datetime'),
      headerAlign: 'center',
      align: 'center',
      width: 200,
      sortable: false,
      valueFormatter: (params: GridValueFormatterParams) =>
        convertStringToDateFormat(
          params.value as string,
          'yyyy-MM-dd HH:mm:ss',
        ),
    },
    {
      field: 'locationId',
      headerName: t('common.manage'),
      headerAlign: 'center',
      align: 'center',
      width: 200,
      sortable: false,
      renderCell: (params: GridCellParams) => (
        <GridButtons
          id={params.value as string}
          handleDelete={handleDelete}
          handleUpdate={handleUpdate}
        />
      ),
    },
  ]
}

const Location = () => {
  const classes = useStyles()
  const { t } = useTranslation()
  const router = useRouter()

  //조회조건 상태관리
  const keywordState = useRecoilValue(conditionAtom(conditionKey))
  // 에러 상태관리
  const setErrorState = useSetRecoilState(errorStateSelector)
  // pagination 상태관리
  const { page, setPageValue } = usePage(conditionKey)
  //조회조건 select items
  const searchTypes = useSearchTypes([
    {
      key: 'locationName',
      label: t('location.name'),
    },
  ])

  //목록 데이터 조회 및 관리
  const { data, mutate } = locationService.search({
    keywordType: keywordState?.keywordType || 'locationName',
    keyword: keywordState?.keyword || '',
    size: GRID_PAGE_SIZE,
    page,
  })

  //목록 조회
  const handleSearch = () => {
    if (page === 0) {
      mutate()
    } else {
      setPageValue(0)
    }
  }

  const handleRegister = () => {
    router.push('location/-1')
  }

  //datagrid page change event
  const handlePageChange = (page: number, details?: any) => {
    setPageValue(page)
  }

  const handleDelete = async (id: number) => {
    try {
      const result = await locationService.delete(id)
      if (result?.status === 204) {
        mutate()
      }
    } catch (error) {
      setErrorState({ error })
    }
  }

  const handleUpdate = (id: number) => {
    router.push(`location/${id}`)
  }

  //사용여부 toggle 시 바로 update
  const toggleIsUse = useCallback(
    async (event: React.ChangeEvent<HTMLInputElement>, id: number) => {
      try {
        const result = await locationService.updateUse(id, event.target.checked)
        if (result?.status === 204) {
          mutate()
        }
      } catch (error) {
        setErrorState({ error })
      }
    },
    [page],
  )

  // 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const columns = useMemo(() => {
    return getColumns(data, handleDelete, handleUpdate, toggleIsUse, t)
  }, [data])

  return (
    <div className={classes.root}>
      <Search
        keywordTypeItems={searchTypes}
        handleSearch={handleSearch}
        handleRegister={handleRegister}
        conditionKey={conditionKey}
      />
      <CustomDataGrid
        classes={classes}
        rows={data?.content}
        columns={columns}
        rowCount={data?.totalElements}
        paginationMode="server"
        pageSize={GRID_PAGE_SIZE}
        page={page}
        onPageChange={handlePageChange}
        getRowId={r => r.locationId}
      />
    </div>
  )
}

export default Location
