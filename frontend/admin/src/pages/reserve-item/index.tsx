import { GridButtons } from '@components/Buttons'
import { PopupProps } from '@components/DialogPopup'
import Search from '@components/Search'
import CustomDataGrid from '@components/Table/CustomDataGrid'
import { GRID_PAGE_SIZE } from '@constants'
import usePage from '@hooks/usePage'
import useSearchTypes from '@hooks/useSearchType'
import { convertStringToDateFormat } from '@libs/date'
import Box from '@material-ui/core/Box'
import Button from '@material-ui/core/Button'
import MenuItem from '@material-ui/core/MenuItem'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import TextField from '@material-ui/core/TextField'
import {
  GridCellParams,
  GridColDef,
  GridValueFormatterParams,
  GridValueGetterParams,
} from '@material-ui/data-grid'
import { ICode, ILocation, locationService, reserveItemService } from '@service'
import { conditionAtom, conditionValue, errorStateSelector } from '@stores'
import { Page, rownum } from '@utils'
import { GetServerSideProps } from 'next'
import { useRouter } from 'next/router'
import React, { useCallback, useMemo, useState } from 'react'
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
    search: {
      padding: theme.spacing(1),
      textAlign: 'center',
      width: '10vw',
      maxWidth: 100,
      minWidth: 80,
    },
  }),
)
const conditionKey = 'reserve-item'

type ColumnType = (
  data: Page,
  handleUpdate: (id: number) => void,
  toggleIsUse: (event: React.ChangeEvent<HTMLInputElement>, id: number) => void,
  t: TFunction,
  handlePopup?: (row: any) => void,
) => GridColDef[]

//그리드 컬럼 정의
const getColumns: ColumnType = (
  data: Page,
  handleUpdate: (id: number) => void,
  toggleIsUse: (event: React.ChangeEvent<HTMLInputElement>, id: number) => void,
  t,
  handlePopup?: (row: any) => void,
) => {
  return [
    {
      field: 'rownum',
      headerName: t('common.no'),
      headerAlign: 'center',
      align: 'center',
      flex: 0.5,
      sortable: false,
      valueGetter: (params: GridValueGetterParams) =>
        rownum(data, params.api.getRowIndex(params.id), 'asc'),
    },
    {
      field: 'locationName',
      headerName: t('location'),
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      sortable: false,
    },
    {
      field: 'categoryName',
      headerName: t('reserve_item.type'),
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      sortable: false,
    },
    {
      field: 'reserveItemName',
      headerName: t('reserve_item.name'),
      headerAlign: 'center',
      align: 'left',
      flex: 1.5,
      sortable: false,
    },
    {
      field: 'totalQty',
      headerName: `${t('reserve.count')}/${t('reserve.number_of_people')}`,
      headerAlign: 'center',
      align: 'right',
      flex: 0.8,
      sortable: false,
    },
    {
      field: 'isUse',
      headerName: t('common.use_at'),
      headerAlign: 'center',
      align: 'center',
      hide: handlePopup ? true : false,
      sortable: false,
      flex: 1,
      renderCell: (params: GridCellParams) => (
        <Switch
          checked={Boolean(params.value)}
          onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
            toggleIsUse(event, params.row.reserveItemId)
          }
        />
      ),
    },
    {
      field: 'createDate',
      headerName: t('common.created_datetime'),
      headerAlign: 'center',
      align: 'center',
      sortable: false,
      flex: 1,
      valueFormatter: (params: GridValueFormatterParams) =>
        convertStringToDateFormat(
          params.value as string,
          'yyyy-MM-dd HH:mm:ss',
        ),
    },
    {
      field: 'reserveItemId',
      headerName: handlePopup ? t('common.select') : t('common.manage'),
      headerAlign: 'center',
      align: 'center',
      sortable: false,
      renderCell: (params: GridCellParams) => {
        return handlePopup ? (
          <Button
            onClick={() => {
              handlePopup(params.row)
            }}
            variant="outlined"
            color="inherit"
            size="small"
          >
            {t('common.select')}
          </Button>
        ) : (
          <GridButtons
            id={params.value as string}
            handleUpdate={handleUpdate}
          />
        )
      },
    },
  ]
}

export type ReserveItemProps = PopupProps & {
  locations?: ILocation[]
  categories?: ICode[]
}

const ReserveItem = (props: ReserveItemProps) => {
  const { handlePopup, locations, categories } = props
  const classes = useStyles()
  const { t } = useTranslation()
  const router = useRouter()

  //조회조건 상태관리
  const keywordState = useRecoilValue(conditionAtom(conditionKey))
  // 에러 상태관리
  const setErrorState = useSetRecoilState(errorStateSelector)

  const [customKeyword, setCustomKeyword] = useState<conditionValue | null>({
    locationId: keywordState?.locationId || '0',
    categoryId: keywordState?.categoryId || 'all',
  })
  const { page, setPageValue } = usePage(conditionKey, 0)

  //조회조건 select items
  const searchTypes = useSearchTypes([
    {
      key: 'item',
      label: t('reserve_item.name'),
    },
  ])

  //목록 데이터 조회 및 관리
  const { data, mutate } = reserveItemService.search({
    keywordType: keywordState?.keywordType || 'item',
    keyword: keywordState?.keyword || '',
    size: GRID_PAGE_SIZE,
    page,
    locationId:
      keywordState?.locationId !== '0' ? keywordState?.locationId : null,
    categoryId:
      keywordState?.categoryId !== 'all' ? keywordState?.categoryId : null,
    isUse: Boolean(handlePopup),
    isPopup: Boolean(handlePopup),
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
    router.push('/reserve-item/-1')
  }

  //datagrid page change event
  const handlePageChange = (page: number, details?: any) => {
    setPageValue(page)
  }

  const handleUpdate = (id: number) => {
    router.push(`/reserve-item/${id}`)
  }

  const handleCategoryChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCustomKeyword({
      ...customKeyword,
      categoryId: e.target.value,
    })
  }

  const handleLocationChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCustomKeyword({
      ...customKeyword,
      locationId: e.target.value,
    })
  }

  //사용여부 toggle 시 바로 update
  const toggleIsUse = useCallback(
    async (event: React.ChangeEvent<HTMLInputElement>, id: number) => {
      try {
        const result = await reserveItemService.updateUse(
          id,
          event.target.checked,
        )
        if (result?.status === 204) {
          mutate()
        }
      } catch (error) {
        setErrorState({ error })
      }
    },
    [customKeyword, page],
  )

  // 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const columns = useMemo(() => {
    return getColumns(data, handleUpdate, toggleIsUse, t, handlePopup)
  }, [data])

  return (
    <div className={classes.root}>
      <Search
        keywordTypeItems={searchTypes}
        handleSearch={handleSearch}
        handleRegister={handlePopup ? undefined : handleRegister}
        conditionKey={conditionKey}
        isNotWrapper={true}
        customKeyword={customKeyword}
        conditionNodes={
          <>
            <Box className={classes.search}>
              <TextField
                id="select-location"
                select
                value={customKeyword.locationId}
                onChange={handleLocationChange}
                variant="outlined"
                fullWidth
              >
                <MenuItem key="location-all" value="0">
                  <em>{t('common.all')}</em>
                </MenuItem>
                {locations &&
                  locations.map(option => (
                    <MenuItem key={option.locationId} value={option.locationId}>
                      {option.locationName}
                    </MenuItem>
                  ))}
              </TextField>
            </Box>
            <Box className={classes.search}>
              <TextField
                id="select-category"
                select
                value={customKeyword.categoryId}
                onChange={handleCategoryChange}
                variant="outlined"
                fullWidth
              >
                <MenuItem key="category-all" value="all">
                  <em>{t('common.all')}</em>
                </MenuItem>
                {categories &&
                  categories.map(option => (
                    <MenuItem key={option.codeId} value={option.codeId}>
                      {option.codeName}
                    </MenuItem>
                  ))}
              </TextField>
            </Box>
          </>
        }
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
        getRowId={r => r.reserveItemId}
      />
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async ({ query }) => {
  let locations: ILocation[] = []
  let categories: ICode[] = []

  try {
    locations = await (await locationService.getList()).data
    categories = await (
      await reserveItemService.getCode('reserve-category')
    ).data
  } catch (error) {
    console.error(`reserve item query error ${error.message}`)
  }

  return {
    props: {
      categories,
      locations,
    },
  }
}

export default ReserveItem
