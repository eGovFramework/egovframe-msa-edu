import { GridButtons } from '@components/Buttons'
import Search from '@components/Search'
// 내부 컴포넌트 및 custom hook, etc...
import CustomDataGrid from '@components/Table/CustomDataGrid'
import { GRID_PAGE_SIZE } from '@constants'
import usePage from '@hooks/usePage'
import useSearchTypes from '@hooks/useSearchType'
import Box from '@material-ui/core/Box'
import Button from '@material-ui/core/Button'
// material-ui deps
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import {
  GridCellParams,
  GridColDef,
  GridValueGetterParams,
} from '@material-ui/data-grid'
//api
import { codeService } from '@service'
import { conditionAtom, errorStateSelector } from '@stores'
import { Page, rownum } from '@utils'
import { AxiosError } from 'axios'
import { NextPage } from 'next'
import { TFunction, useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { useCallback, useMemo } from 'react'
// 상태관리 recoil
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

//그리드 컬럼 정의
type ColumnsType = (
  data: Page,
  routeCodeDetail: (id: string) => void,
  deleteCode: (id: string) => void,
  updateCode: (id: string) => void,
  toggleIsUse: (event: React.ChangeEvent<HTMLInputElement>, id: string) => void,
  t?: TFunction,
) => GridColDef[]

const getColumns: ColumnsType = (
  data,
  routeCodeDetail,
  deleteCode,
  updateCode,
  toggleIsUse,
  t,
) => {
  return [
    {
      field: 'rownum',
      headerName: t('common.no'), // 번호
      headerAlign: 'center',
      align: 'center',
      sortable: false,
      valueGetter: (params: GridValueGetterParams) =>
        rownum(data, params.api.getRowIndex(params.id), 'desc'),
    },
    {
      field: 'codeId',
      headerName: t('code.code_id'), // 코드ID
      headerAlign: 'center',
      width: 150,
      sortable: false,
    },
    {
      field: 'codeName',
      headerName: t('code.code_name'), // 코드명
      headerAlign: 'center',
      width: 200,
      sortable: false,
    },
    {
      field: 'useAt',
      headerName: t('common.use_at'), // 사용여부
      headerAlign: 'center',
      align: 'center',
      width: 120,
      sortable: false,
      renderCell: (params: GridCellParams) => (
        <Switch
          checked={Boolean(params.value)}
          onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
            toggleIsUse(event, params.row.codeId)
          }
        />
      ),
    },
    {
      field: 'codeDetailCount',
      headerName: t('code.detail_count'), // 코드상세수
      headerAlign: 'center',
      align: 'center',
      width: 120,
      sortable: false,
    },
    {
      field: 'id',
      headerName: t('common.manage'), // 관리
      headerAlign: 'center',
      align: 'center',
      width: 300,
      sortable: false,
      renderCell: (params: GridCellParams) => (
        <>
          <Box mr={1}>
            <Button
              variant="outlined"
              color="default"
              size="small"
              onClick={() => routeCodeDetail(params.row.codeId)}
            >
              {t('code.detail.list')}
            </Button>
          </Box>
          <GridButtons
            id={params.row.codeId as string}
            handleUpdate={updateCode}
            handleDelete={deleteCode}
          />
        </>
      ),
    },
  ]
}

const conditionKey = 'code'

// 실제 render되는 컴포넌트
const Code: NextPage = () => {
  // props 및 전역변수
  // const { id } = props
  const classes = useStyles()
  const route = useRouter()

  const { t } = useTranslation()

  //조회조건 select items
  const searchTypes = useSearchTypes([
    {
      key: 'codeId',
      label: t('code.code_id'),
    },
    {
      key: 'codeName',
      label: t('code.code_name'),
    },
  ])

  /**
   * 상태관리 필요한 훅
   */
  //조회조건 상태관리
  const keywordState = useRecoilValue(conditionAtom(conditionKey))
  const setErrorState = useSetRecoilState(errorStateSelector)

  //현 페이지내 필요한 hook
  const { page, setPageValue } = usePage(conditionKey)

  //목록 데이터 조회 및 관리
  const { data, mutate } = codeService.search({
    keywordType: keywordState?.keywordType || 'codeId',
    keyword: keywordState?.keyword || '',
    size: GRID_PAGE_SIZE,
    page,
  })

  /**
   * 비지니스 로직
   */

  //에러 callback
  const errorCallback = useCallback((error: AxiosError) => {
    setErrorState({
      error,
    })
  }, [])

  // 코드상세목록
  const routeCodeDetail = useCallback((id: string) => {
    route.push(
      {
        pathname: '/code/detail',
        query: {
          parentCodeId: id,
        },
      },
      '/code/detail',
    )
  }, [])

  //삭제
  const deleteCode = useCallback((id: string) => {
    codeService.delete({
      callback: mutate,
      errorCallback,
      id,
    })
  }, [])

  //수정 시 상세 화면 이동
  const updateCode = useCallback((id: string) => {
    route.push(`/code/${id}`)
  }, [])

  //사용여부 toggle 시 바로 update
  const toggleIsUse = useCallback(
    async (event: React.ChangeEvent<HTMLInputElement>, id: string) => {
      codeService.updateUse({
        callback: mutate,
        errorCallback,
        id,
        useAt: event.target.checked,
      })
    },
    [page],
  )

  // 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const columns = useMemo(() => {
    return getColumns(
      data,
      routeCodeDetail,
      deleteCode,
      updateCode,
      toggleIsUse,
      t,
    )
  }, [data])

  //목록 조회
  const handleSearch = () => {
    if (page === 0) {
      mutate(data, false)
    } else {
      setPageValue(0)
    }
  }

  //datagrid page change event
  const handlePageChange = (page: number, details?: any) => {
    setPageValue(page)
  }

  return (
    <div className={classes.root}>
      <Search
        keywordTypeItems={searchTypes}
        handleSearch={handleSearch}
        handleRegister={() => {
          route.push('code/-1')
        }}
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
        getRowId={r => r.codeId}
      />
    </div>
  )
}

export default Code
