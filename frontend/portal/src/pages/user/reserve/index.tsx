import {
  OptionsType,
  SelectBox,
  SelectType,
} from '@components/Inputs/SelectBox'
import Search from '@components/Search'
import DataGridTable from '@components/TableList/DataGridTable'
import { DEFUALT_GRID_PAGE_SIZE, GRID_ROWS_PER_PAGE_OPTION } from '@constants'
import usePage from '@hooks/usePage'
import useSearchTypes from '@hooks/useSearchTypes'
import { convertStringToDateFormat } from '@libs/date'
import Typography from '@material-ui/core/Typography'
import {
  GridCellParams,
  GridColDef,
  GridValueFormatterParams,
  GridValueGetterParams,
  MuiEvent,
} from '@material-ui/data-grid'
import { ICode, ILocation, Page, reserveService } from '@service'
import { conditionAtom, conditionValue, userAtom } from '@stores'
import { rownum } from '@utils'
import { GetServerSideProps } from 'next'
import { useRouter } from 'next/router'
import React, { createRef, useMemo, useState } from 'react'
import { TFunction, useTranslation } from 'react-i18next'
import { useRecoilValue } from 'recoil'

type ColorType =
  | 'inherit'
  | 'initial'
  | 'primary'
  | 'secondary'
  | 'textPrimary'
  | 'textSecondary'
  | 'error'

type ColumnsType = (
  data: Page,
  locations: OptionsType[],
  categories: OptionsType[],
  status: ICode[],
  t?: TFunction,
) => GridColDef[]

const getColumns: ColumnsType = (data, locations, categories, status, t) => {
  return [
    {
      field: 'rownum',
      headerName: t('common.no'),
      headerAlign: 'center',
      align: 'center',
      sortable: false,
      valueGetter: (params: GridValueGetterParams) =>
        rownum(data, params.api.getRowIndex(params.id), 'desc'),
    },
    {
      field: 'locationId',
      headerName: t('location'),
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      sortable: false,
      renderCell: (params: GridCellParams) => (
        <>{locations.find(item => item.value === params.value)?.label}</>
      ),
    },
    {
      field: 'categoryId',
      headerName: t('reserve_item.type'),
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      sortable: false,
      renderCell: (params: GridCellParams) => (
        <>{categories.find(item => item.value === params.value)?.label}</>
      ),
    },
    {
      field: 'reserveItemName',
      headerName: t('reserve_item.name'),
      headerAlign: 'center',
      align: 'left',
      flex: 1,
      sortable: false,
      cellClassName: 'title',
    },
    {
      field: 'reserveQty',
      headerName: `${t('reserve.count')}/${t('reserve.number_of_people')} (${t(
        'reserve_item.inventory',
      )})`,
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      sortable: false,
      renderCell: (params: GridCellParams) => {
        const category = params.row.categoryId
        if (category === 'education') {
          return `${params.value}(${params.row.totalQty})`
        } else {
          return `${params.row.totalQty}`
        }
      },
    },
    {
      field: 'reserveStatusId',
      headerName: t('reserve.status'),
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      sortable: false,
      renderCell: (params: GridCellParams) => {
        let color: ColorType = 'inherit'
        if (params.value === 'request' || params.value === 'cancel') {
          color = 'error'
        }
        return (
          <Typography color={color}>
            {status.find(item => item.codeId === params.value)?.codeName}
          </Typography>
        )
      },
    },
    {
      field: 'createDate',
      headerName: t('common.created_date'),
      headerAlign: 'center',
      align: 'center',
      minWidth: 140,
      sortable: false,
      valueFormatter: (params: GridValueFormatterParams) =>
        params.value
          ? convertStringToDateFormat(String(params.value), 'yyyy-MM-dd HH:mm')
          : null,
    },
  ]
}

const getXsColumns: ColumnsType = (data, locations, categories, status, t) => {
  return [
    {
      field: 'reserveItemName',
      headerName: t('reserve_item.name'),
      headerAlign: 'center',
      sortable: false,
      renderCell,
    },
  ]

  function renderCell(params: GridCellParams) {
    return (
      <div>
        <div className="title">{params.value}</div>
        <div className="sub">
          <p>
            {
              locations.find(item => item.value === params.row.locationId)
                ?.label
            }
          </p>
          <p>
            {
              categories.find(item => item.value === params.row.categoryId)
                ?.label
            }
          </p>

          <p>
            {params.row.reserveStatusId === 'request' ||
            params.row.reserveStatusId === 'cancel' ? (
              <Typography component="span" color="error">
                {
                  status.find(
                    item => item.codeId === params.row.reserveStatusId,
                  ).codeName
                }
              </Typography>
            ) : (
              <Typography component="span">
                {
                  status.find(
                    item => item.codeId === params.row.reserveStatusId,
                  ).codeName
                }
              </Typography>
            )}
          </p>
        </div>
      </div>
    )
  }
}

const conditionKey = 'user-reserve'

interface UserReserveProps {
  locations: OptionsType[]
  categories: OptionsType[]
  status: ICode[]
}

const UserReserve = (props: UserReserveProps) => {
  const { locations, categories, status } = props

  const router = useRouter()
  const { t } = useTranslation()

  // 조회조건 상태관리
  const keywordState = useRecoilValue(conditionAtom(conditionKey))
  const pageSizeRef = createRef<SelectType>()

  const user = useRecoilValue(userAtom)
  const { page, setPageValue } = usePage(conditionKey, 0)
  const [pageSize, setPageSize] = useState<number>(DEFUALT_GRID_PAGE_SIZE)

  const [customKeyword, setCustomKeyword] = useState<conditionValue | null>({
    locationId: keywordState?.locationId || '0',
    categoryId: keywordState?.categoryId || 'all',
  })

  // 조회조건 select items
  const searchTypes = useSearchTypes([
    {
      value: 'item',
      label: t('reserve_item.name'),
    },
  ])

  const { data, mutate } = reserveService.searchUserReserve({
    userId: user?.userId,
    keywordType: keywordState?.keywordType || 'item',
    keyword: keywordState?.keyword || '',
    size: pageSize,
    page,
    locationId:
      keywordState?.locationId !== '0' ? keywordState?.locationId : null,
    categoryId:
      keywordState?.categoryId !== 'all' ? keywordState?.categoryId : null,
  })

  const handleSearch = () => {
    if (page === 0) {
      mutate(data, false)
    } else {
      setPageValue(0)
    }
  }

  const handlePageChange = (page: number, details?: any) => {
    setPageValue(page)
  }

  const handlePageSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setPageSize(Number(e.target.value))
  }

  const handleCellClick = (
    params: GridCellParams,
    event: MuiEvent<React.MouseEvent>,
    details?: any,
  ) => {
    if (params.field === 'reserveItemName') {
      router.push(`${router.asPath}/${params.id}`)
    }
  }

  const handleLocationChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setCustomKeyword({
      ...customKeyword,
      locationId: e.target.value,
    })
  }

  const handleCategoryChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setCustomKeyword({
      ...customKeyword,
      categoryId: e.target.value,
    })
  }

  const columns = useMemo(
    () => getColumns(data, locations, categories, status, t),
    [data, t],
  )

  const xsColumns = useMemo(
    () => getXsColumns(data, locations, categories, status, t),
    [data, t],
  )

  const rowsPerPageSizeOptinos = GRID_ROWS_PER_PAGE_OPTION.map(item => {
    return {
      value: item,
      label: `${item} 개`,
    }
  })

  return (
    <div className="mypage">
      <div className="table_list01">
        <fieldset>
          <div>
            <SelectBox
              ref={pageSizeRef}
              options={rowsPerPageSizeOptinos}
              onChange={handlePageSizeChange}
            />
          </div>
          <div>
            <Search
              options={searchTypes}
              conditionKey={conditionKey}
              handleSearch={handleSearch}
              customKeyword={customKeyword}
              conditionNodes={
                <>
                  {locations && (
                    <SelectBox
                      options={locations}
                      value={customKeyword.locationId}
                      onChange={handleLocationChange}
                      style={{ marginRight: '2px' }}
                    />
                  )}
                  {categories && (
                    <SelectBox
                      options={categories}
                      value={customKeyword.categoryId}
                      onChange={handleCategoryChange}
                      style={{ marginRight: '2px' }}
                    />
                  )}
                </>
              }
            />
          </div>
        </fieldset>
        <DataGridTable
          columns={columns}
          rows={data?.content}
          xsColumns={xsColumns}
          getRowId={r => r.reserveId}
          pageSize={pageSize}
          rowCount={data?.totalElements}
          page={page}
          onPageChange={handlePageChange}
          paginationMode="server"
          onCellClick={handleCellClick}
        />
      </div>
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async context => {
  const categoryId = String(context.query.category)

  let locations: OptionsType[] = []
  let categories: OptionsType[] = []
  let status: ICode[] = []

  try {
    const location = (await (
      await reserveService.getLocation()
    ).data) as ILocation[]
    if (location) {
      locations = location.map(item => {
        return {
          value: item.locationId,
          label: item.locationName,
        }
      })
      locations.unshift({
        value: '0',
        label: '전체',
      })
    }

    const category = (await (
      await reserveService.getCode('reserve-category')
    ).data) as ICode[]
    if (category) {
      categories = category.map(item => {
        return {
          value: item.codeId,
          label: item.codeName,
        }
      })
      categories.unshift({
        value: 'all',
        label: '전체',
      })
    }

    const codeResult = await reserveService.getCode('reserve-status')
    if (codeResult) {
      status = codeResult.data
    }
  } catch (error) {
    console.error(`reserve detail item query error ${error.message}`)
  }

  return {
    props: {
      locations,
      categories,
      status,
    },
  }
}

export default UserReserve
