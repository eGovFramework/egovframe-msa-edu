import { OptionsType, SelectBox, SelectType } from '@components/Inputs'
import Search from '@components/Search'
import DataGridTable from '@components/TableList/DataGridTable'
import { DEFUALT_GRID_PAGE_SIZE, GRID_ROWS_PER_PAGE_OPTION } from '@constants'
import usePage from '@hooks/usePage'
import useSearchTypes from '@hooks/useSearchTypes'
import Typography from '@material-ui/core/Typography'
import {
  GridCellParams,
  GridColDef,
  GridValueGetterParams,
  MuiEvent,
} from '@material-ui/data-grid'
import { ICode, ILocation, Page, reserveService } from '@service'
import { conditionAtom, conditionValue } from '@stores'
import { rownum } from '@utils'
import { GetServerSideProps } from 'next'
import { TFunction, useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { createRef, useMemo, useState } from 'react'
import { useRecoilValue } from 'recoil'

type ColumnsType = (data: Page, t?: TFunction) => GridColDef[]

const getColumns: ColumnsType = (data, t) => {
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
      flex: 1,
      sortable: false,
      cellClassName: 'title',
    },
    {
      field: 'isPossible',
      headerName: t('reserve_item.is_possible'),
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      sortable: false,
      renderCell: (params: GridCellParams) => (
        <>
          {Boolean(params.value) === true ? (
            <Typography color="error">{t('reserve_item.possible')}</Typography>
          ) : (
            <Typography>{t('reserve_item.impossible')}</Typography>
          )}
        </>
      ),
    },
  ]
}

const getXsColumns: ColumnsType = (data, t) => {
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
          <p>{params.row.locationName}</p>
          <p>{params.row.categoryName}</p>

          <p>
            {Boolean(params.row.isPossible) ? (
              <Typography component="span" color="error">
                {t('reserve_item.possible')}
              </Typography>
            ) : (
              <Typography component="span">
                {t('reserve_item.impossible')}
              </Typography>
            )}
          </p>
        </div>
      </div>
    )
  }
}

const conditionKey = 'reserve'

interface ReserveProps {
  locations: OptionsType[]
  categories: OptionsType[]
}

const Reserve = ({ locations, categories }: ReserveProps) => {
  const router = useRouter()
  const { t } = useTranslation()

  // 조회조건 상태관리
  const keywordState = useRecoilValue(conditionAtom(conditionKey))
  const pageSizeRef = createRef<SelectType>()

  const locationRef = createRef<SelectType>()
  const categoryRef = createRef<SelectType>()

  const { page, setPageValue } = usePage(conditionKey, 0)
  const [pageSize, setPageSize] = useState<number>(DEFUALT_GRID_PAGE_SIZE)

  const [customKeyword, setCustomKeyword] = useState<conditionValue | null>({
    locationId: keywordState?.locationId || '0',
    categoryId:
      router.query?.category === 'all'
        ? keywordState?.categoryId || 'all'
        : String(router.query?.category),
  })

  // 조회조건 select items
  const searchTypes = useSearchTypes([
    {
      value: 'item',
      label: t('reserve_item.name'),
    },
  ])

  const { data, mutate } = reserveService.search({
    keywordType: keywordState?.keywordType || 'item',
    keyword: keywordState?.keyword || '',
    size: pageSize,
    page,
    locationId:
      keywordState?.locationId !== '0' ? keywordState?.locationId : null,
    categoryId:
      router.query?.category !== 'all'
        ? String(router.query?.category)
        : keywordState?.categoryId !== 'all'
        ? keywordState?.categoryId
        : null,
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

  const columns = useMemo(() => getColumns(data, t), [data, t])

  const xsColumns = useMemo(() => getXsColumns(data, t), [data, t])

  const rowsPerPageSizeOptinos = GRID_ROWS_PER_PAGE_OPTION.map(item => {
    return {
      value: item,
      label: `${item} 개`,
    }
  })

  return (
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
            className={router.query?.category === 'all' ? '' : 'wide'}
            conditionNodes={
              <>
                {locations && (
                  <SelectBox
                    ref={locationRef}
                    options={locations}
                    value={customKeyword.locationId}
                    onChange={handleLocationChange}
                    style={{ marginRight: '2px' }}
                  />
                )}
                {router.query?.category === 'all' && categories && (
                  <SelectBox
                    ref={categoryRef}
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
        getRowId={r => r.reserveItemId}
        pageSize={pageSize}
        rowCount={data?.totalElements}
        page={page}
        onPageChange={handlePageChange}
        paginationMode="server"
        onCellClick={handleCellClick}
      />
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async context => {
  const categoryId = String(context.query.category)

  let locations: OptionsType[]
  let categories: OptionsType[]

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
  } catch (error) {
    console.error(`reserve detail item query error ${error.message}`)
  }

  return {
    props: {
      locations,
      categories,
    },
  }
}

export default Reserve
