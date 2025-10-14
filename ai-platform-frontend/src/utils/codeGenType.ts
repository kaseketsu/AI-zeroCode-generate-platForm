export const codeGenTypeEnums = {
  HTML: 'html',
  MULTI_FILE: 'html-multi',
}

export const codeGenTypeConfig = {
  [codeGenTypeEnums.HTML]: {
    label: '原生 HTML 模式',
    value: codeGenTypeEnums.HTML,
  },
  [codeGenTypeEnums.MULTI_FILE]: {
    label: '原生多文件模式',
    value: codeGenTypeEnums.MULTI_FILE,
  },
}

export const codeGenTypeOptions = Object.values(codeGenTypeConfig).map((config) => ({
  label: config.label,
  value: config.value,
}))
