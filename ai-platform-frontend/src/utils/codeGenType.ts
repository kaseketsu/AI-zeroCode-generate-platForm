export const codeGenTypeEnums = {
  HTML: 'html',
  MULTI_FILE: 'html-multi',
  VUE_PROJECT: 'vue_project',
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
  [codeGenTypeEnums.VUE_PROJECT]: {
    label: 'vue 项目模式',
    value: codeGenTypeEnums.VUE_PROJECT,
  }
}

export const codeGenTypeOptions = Object.values(codeGenTypeConfig).map((config) => ({
  label: config.label,
  value: config.value,
}))
