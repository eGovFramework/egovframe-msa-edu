import React, { createRef, useEffect, useState } from 'react'
import { makeStyles, createStyles, Theme } from '@material-ui/core/styles'
import Box from '@material-ui/core/Box'
import TextField from '@material-ui/core/TextField'
import MenuItem from '@material-ui/core/MenuItem'
import IconButton from '@material-ui/core/IconButton'
import SearchIcon from '@material-ui/icons/Search'
import Fab from '@material-ui/core/Fab'
import AddIcon from '@material-ui/icons/Add'
import { conditionAtom, conditionSelector, conditionValue } from '@stores'
import { useRecoilValue, useSetRecoilState } from 'recoil'

// styles
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
    },
    select: {
      padding: theme.spacing(1),
      textAlign: 'center',
      width: '15vw',
      minWidth: 80,
      maxWidth: 150,
    },
    iconButton: {
      padding: theme.spacing(1),
      marginLeft: theme.spacing(1),
      backgroundColor: theme.palette.background.default,
    },
    fab: {
      marginLeft: theme.spacing(1),
    },
  }),
)

// 조회조건 타입
export interface ICondition {
  keywordType: string
  keyword: string
}

// 조회조건 select 아이템 타입
export interface IKeywordType {
  key: string
  label: string
}

// 조회조건 컴포넌트 props
export interface ISearchProp {
  keywordTypeItems: IKeywordType[] // 조회조건 select items
  handleSearch: () => void // 조회 시
  handleRegister?: () => void // 등록 시
  conditionKey: string // 조회조건 상태값을 관리할 키 값 (e.g. 이용약관관리 -> policy)
  isNotWrapper?: boolean
  customKeyword?: conditionValue
  conditionNodes?: React.ReactNode
}

const Search = (props: ISearchProp) => {
  const {
    keywordTypeItems,
    handleSearch,
    handleRegister,
    conditionKey,
    customKeyword,
    isNotWrapper,
    conditionNodes,
  } = props
  const classes = useStyles()

  // 조회조건에 대한 키(conditionKey)로 각 기능에서 조회조건 상태값을 관리한다.
  const setValue = useSetRecoilState(conditionSelector(conditionKey))
  const conditionState = useRecoilValue(conditionAtom(conditionKey))
  const [keywordTypeState, setKeywordTypeState] = useState<string>('')
  const inputRef = createRef<HTMLInputElement>()

  useEffect(() => {
    if (conditionState) {
      setKeywordTypeState(conditionState.keywordType)
      return
    }

    if (keywordTypeItems.length > 0) {
      setKeywordTypeState(keywordTypeItems[0].key)
      return
    }
  }, [conditionState, keywordTypeItems])

  // 조회 시 조회조건 상태값 저장 후 부모컴포넌트의 조회 함수를 call한다.
  const search = () => {
    setValue({
      ...conditionState,
      keywordType: keywordTypeState,
      keyword: inputRef.current?.value,
      ...customKeyword,
    })
    handleSearch()
  }

  // 조회조건 select onchange
  const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setKeywordTypeState(event.target.value)
  }

  // 조회조건 input에서 enter키 눌렀을 경우 조회
  const onKeyPress = (event: React.KeyboardEvent<HTMLElement>) => {
    if (event.key === 'Enter') {
      event.preventDefault()
      search()
    }
  }

  // 조회 버튼 클릭
  const onClickSearch = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault()
    search()
  }

  // 등록 버튼 클릭
  const onClickAdd = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault()
    handleRegister()
  }

  return (
    <div className={classes.root}>
      <Box display="flex" flexDirection="row" justifyContent="flex-end">
        {conditionNodes && conditionNodes}
        <Box className={classes.select}>
          <TextField
            id="filled-select-currency"
            select
            value={keywordTypeState}
            onChange={onChange}
            variant="outlined"
            fullWidth
          >
            {keywordTypeItems.map(option => (
              <MenuItem key={option.key} value={option.key}>
                {option.label}
              </MenuItem>
            ))}
          </TextField>
        </Box>
        <Box width="auto" className={classes.search}>
          <TextField
            inputRef={inputRef}
            placeholder="Search..."
            inputProps={{ 'aria-label': 'search' }}
            variant="outlined"
            onKeyPress={onKeyPress}
            defaultValue={conditionState ? conditionState.keyword : ''}
          />
          <IconButton
            className={classes.iconButton}
            aria-label="search"
            color="primary"
            onClick={onClickSearch}
          >
            <SearchIcon />
          </IconButton>
          {handleRegister && (
            <Fab
              color="primary"
              aria-label="add"
              className={classes.fab}
              size="small"
              onClick={onClickAdd}
            >
              <AddIcon />
            </Fab>
          )}
        </Box>
      </Box>
    </div>
  )
}

export default Search
