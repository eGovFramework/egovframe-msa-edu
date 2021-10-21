import { GridButtons } from '@components/Buttons'
import Search from '@components/Search'
// 내부 컴포넌트 및 custom hook, etc...
import CustomDataGrid from '@components/Table/CustomDataGrid'
import { GRID_PAGE_SIZE } from '@constants'
import usePage from '@hooks/usePage'
import useSearchTypes from '@hooks/useSearchType'
import Box from '@material-ui/core/Box'
import MenuItem from '@material-ui/core/MenuItem'
// material-ui deps
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import TextField from '@material-ui/core/TextField'
import {
  GridCellParams,
  GridColDef,
  GridValueGetterParams,
} from '@material-ui/data-grid'
//api
import { codeService, ICode } from '@service'
import { conditionAtom, conditionValue, errorStateSelector } from '@stores'
import { Page, rownum } from '@utils'
import { AxiosError } from 'axios'
import { GetServerSideProps, NextPage } from 'next'
import { TFunction } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { useCallback, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
// 상태관리 recoil
import { useRecoilState, useSetRecoilState } from 'recoil'

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
      width: '20vw',
      minWidth: 80,
      maxWidth: 200,
    },
  }),
)

//그리드 컬럼 정의
type ColumnsType = (
  data: Page,
  deleteCode: (id: string) => void,
  updateCode: (id: string) => void,
  toggleIsUse: (event: React.ChangeEvent<HTMLInputElement>, id: string) => void,
  t?: TFunction,
) => GridColDef[]

const getColumns: ColumnsType = (
  data,
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
      field: 'parentCodeId',
      headerName: t('code.code_id'), // 코드ID
      headerAlign: 'center',
      width: 200,
      sortable: false,
    },
    {
      field: 'codeId',
      headerName: t('code.code'), // 코드
      headerAlign: 'center',
      width: 200,
      sortable: false,
    },
    {
      field: 'codeName',
      headerName: t('code.code_name'), // 코드명
      headerAlign: 'center',
      width: 300,
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
      field: 'id',
      headerName: t('common.manage'), // 관리
      headerAlign: 'center',
      align: 'center',
      width: 200,
      sortable: false,
      renderCell: (params: GridCellParams) => (
        <GridButtons
          id={params.row.codeId as string}
          handleUpdate={updateCode}
          handleDelete={deleteCode}
        />
      ),
    },
  ]
}

interface IParentCodeProps {
  parentCodes: ICode[]
}

const conditionKey = 'code-detail'

// 실제 render되는 컴포넌트
const CodeDetail: NextPage<IParentCodeProps> = ({ parentCodes }) => {
  // props 및 전역변수
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
  const [keywordState, setKeywordState] = useRecoilState(
    conditionAtom(conditionKey),
  )
  const setErrorState = useSetRecoilState(errorStateSelector)

  // 공통코드 관리 기능에서 넘어오는 경우 parameter
  const queryParentCodeId = route.query.parentCodeId as string

  //현 페이지내 필요한 hook
  const { page, setPageValue } = usePage(conditionKey)
  const [customKeyword, setCustomKeyword] = useState<conditionValue>({
    parentCodeId: keywordState?.parentCodeId || queryParentCodeId || '-',
  })

  //목록 데이터 조회 및 관리
  const { data, mutate } = codeService.searchDetail({
    parentCodeId: keywordState?.parentCodeId || queryParentCodeId || '',
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

  //삭제
  const deleteCode = useCallback((id: string) => {
    codeService.deleteDetail({
      callback: mutate,
      errorCallback,
      id,
    })
  }, [])

  //수정 시 상세 화면 이동
  const updateCode = useCallback((id: string) => {
    route.push(`/code/detail/${id}`)
  }, [])

  //사용여부 toggle 시 바로 update
  const toggleIsUse = useCallback(
    async (event: React.ChangeEvent<HTMLInputElement>, id: string) => {
      codeService.updateUseDetail({
        callback: mutate,
        errorCallback,
        id,
        useAt: event.target.checked,
      })
    },
    [page, customKeyword],
  )

  // 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const columns = useMemo(() => {
    return getColumns(data, deleteCode, updateCode, toggleIsUse, t)
  }, [data])

  //목록 조회
  const handleSearch = () => {
    if (page === 0) {
      mutate(data, false)
    } else {
      setPageValue(0)
    }
  }

  // 조회조건 select onchange
  const handleParentCodeIdChange = (
    event: React.ChangeEvent<HTMLInputElement>,
  ) => {
    event.preventDefault()
    setCustomKeyword({
      parentCodeId: event.target.value,
    })
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
          route.push('/code/detail/-1')
        }}
        conditionKey={conditionKey}
        isNotWrapper={true}
        customKeyword={customKeyword}
        conditionNodes={
          <Box className={classes.search}>
            <TextField
              id="select-parentCodeId"
              select
              value={customKeyword.parentCodeId}
              onChange={handleParentCodeIdChange}
              variant="outlined"
              fullWidth
            >
              <MenuItem key="-" value="-">
                <em>{t('code.code_id')}</em>
              </MenuItem>
              {parentCodes.map(option => (
                <MenuItem key={option.codeId} value={option.codeId}>
                  {option.codeName}
                </MenuItem>
              ))}
            </TextField>
          </Box>
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
        getRowId={r => r.codeId}
      />
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async context => {
  let parentCodes = []

  try {
    const codeList = await codeService.getParentCodeList()
    if (codeList) {
      parentCodes = (await codeList.data) as ICode[]
    }
  } catch (error) {
    console.error(`codes query error ${error.message}`)
  }

  return {
    props: {
      parentCodes,
    },
  }
}

export default CodeDetail
