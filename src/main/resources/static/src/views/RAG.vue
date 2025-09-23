<script lang="ts" setup>
import {ref, reactive, computed, onMounted, onUnmounted, watch, nextTick} from 'vue'
import axios from '@/network'
import {msg} from '@/utils/Utils'
import type {FormInstance, FormRules, InputInstance, UploadFile} from 'element-plus'
import {getLangData} from "@/i18n/locale";
import MarkdownIt from 'markdown-it';
import hljs from 'highlight.js';
import 'highlight.js/styles/default.css';
import mk from 'markdown-it-katex';

const langData = getLangData()

const headerCellStyle = () => {
  // 添加表头颜色
  return {backgroundColor: '#f5f5f5', color: '#333', fontWeight: 'bold'}
}

const debounce = (callback: (...args: any[]) => void, delay: number) => {
  let tid: any
  return function (...args: any[]) {
    const ctx = self
    tid && clearTimeout(tid)
    tid = setTimeout(() => {
      callback.apply(ctx, args)
    }, delay)
  }
}

const _ = (window as any).ResizeObserver;
(window as any).ResizeObserver = class ResizeObserver extends _ {
  constructor(callback: (...args: any[]) => void) {
    callback = debounce(callback, 20)
    super(callback)
  }
}

const showContent = ref('')
const showResult = ref(false)
const md = new MarkdownIt({
  html: true,
  breaks: true,
  linkify: false
})
md.disable('normalize')
const processMarkdown = (content: string) => {
  // 将所有换行符统一转为 \n
  const normalized = content.replace(/\r/g, '\n');
  // 确保列表项换行
  return normalized.replace(/\n *-/g, '\n - ');
};
let renderedHTML = ref('')
renderedHTML = computed(() => {
  const processed = processMarkdown(showContent.value);
  return md.render(processed);
});

const form = reactive({
  message: ''
})

const icon = ref('<svg t="1757930319644" class="icon" viewBox="0 0 1089 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="7821" width="14" height="14"><path d="M466.63477617 31.24645905c61.12522043 0.88373813 118.42090898 24.45008817 165.40631938 71.43549856 6.92261532 6.92261532 12.96149253 8.54280189 22.24074285 7.21719472 134.77006433-19.00036972 253.04368363 66.13306981 277.78835118 200.01939602 6.48074626 35.20223538 4.41869063 70.25718107-5.59700813 104.57567833-2.50392469 8.3955122-0.88373813 13.69794096 4.56598031 20.32597691 111.49829366 136.83211996 44.7760651 343.77413131-125.63810368 388.84477577-9.72111939 2.50392469-13.99252034 6.92261532-17.38018316 15.75999661-63.33456576 164.66987094-276.02087492 209.00406699-400.03879204 83.36596328-6.1861669-6.1861669-11.48859565-7.51177407-19.88410786-6.33345656-136.68483027 19.2949491-255.10573925-65.98578013-279.55582743-201.34500323-6.33345658-34.7603663-3.97682157-69.37344294 6.03887721-103.25007113 2.50392469-8.3955122 0.88373813-13.69794096-4.41869062-20.32597692-111.94016272-136.97940963-44.03961664-345.39431787 126.96371087-389.28664484 8.54280189-2.20934532 12.51962347-5.89158752 15.6127069-13.84523064 36.82242196-96.32745582 126.22726243-157.59996593 233.89602422-157.15809688z m148.17342591 593.13557275c-77.32708608 44.6287754-152.1502475 87.49007455-226.38425013 131.08782212-16.79102442 9.86840908-31.07812413 9.5738297-47.86914855-0.29457938-58.17942668-34.46578693-116.80072242-67.90054606-175.42201815-101.48259488-4.27140094-2.35663501-7.80635344-7.2171947-14.58167908-5.15513907-7.95364315 71.58278827 14.4343894 131.23511182 73.20297482 173.50725221 59.2104545 42.56671978 124.3124965 47.86914853 188.82537976 13.69794097 65.98578013-34.907656 129.76221494-74.08671296 194.42238788-111.49829366 3.68224221-2.06205563 7.80635344-3.68224221 7.80635345-9.13196065v-90.73044766z m-41.24111259-487.38157692c-1.47289687-5.15513907-5.30242877-6.62803595-8.24822251-8.69009157-58.91587511-39.9155054-121.66128212-46.24896197-185.29042725-14.87625848-64.21830387 31.81457257-98.38951143 86.16446736-100.30427737 157.89454531-1.91476594 72.90839547-0.29457937 145.81679091-0.73644845 218.72518637 0 7.80635344 2.65121438 11.93046471 9.27925032 15.46541723 21.65158411 11.93046471 43.00858883 24.59737787 64.36559358 36.82242194 2.65121438 1.47289687 5.0078494 4.71327001 9.42654001 1.91476595 0-86.31175705 0.29457937-173.06538315-0.14728967-259.81900928-0.1472897-17.82205222 6.62803595-29.31064787 22.09345315-38.00073943 44.7760651-25.18653662 89.11026111-51.10952167 133.59174684-76.88521704 18.70579035-10.75214722 37.4115807-21.79887379 55.97008135-32.551021z m85.28072925 341.56478599v18.85308004c0 81.89306641-0.1472897 163.93342252 0.14728967 245.82648892 0.1472897 16.64373472-6.03887719 27.98504069-20.62055629 35.93868382-20.47326659 11.19401627-40.50466414 23.12448098-60.68335137 34.76036634-43.45045791 25.18653662-87.04820548 50.37307323-134.62277464 77.91624483 4.12411126 1.47289687 6.1861669 1.76747625 7.65906377 2.94579376 54.64447417 39.9155054 114.29679772 49.04746604 176.89491503 24.00821909 62.5981173-25.03924691 102.95549177-72.6138161 110.61455554-139.63062402 9.27925033-81.89306641 1.91476594-164.66987094 3.3876628-247.00480644 0.1472897-4.41869063-1.91476594-6.92261532-5.44971843-8.98467094-25.03924691-14.4343894-49.93120416-28.86877882-77.32708608-44.6287754zM554.71400945 367.2142369c6.03887719 3.68224221 10.16298847 6.03887719 14.13981004 8.39551221 71.2882089 41.24111259 142.42912809 82.62951485 214.01191636 123.42875836 15.46541722 8.83738127 22.09345317 20.47326659 21.94616348 38.14802915-0.44186907 68.19512544-0.1472897 136.39025089-0.14728969 204.58537633 0 5.0078494 0.44186907 9.86840908 0.73644844 15.90728628 3.68224221-1.3256072 5.89158752-2.06205563 8.10093282-2.94579374 64.0710142-29.75251694 102.07175363-79.6837211 108.11063085-149.79361249 6.33345658-72.6138161-22.68261193-130.94053245-84.98614986-169.38314097-62.745407-38.73718787-127.55286961-74.08671296-191.32930443-111.20371428-5.59700814-3.24037312-9.72111939-3.68224221-15.61270692-0.29457938-24.15550879 14.13981003-48.60559698 27.83775099-74.97045109 43.15587853zM216.24230691 270.44491203c-6.62803595-0.88373813-10.60485751 2.20934532-14.72896877 4.27140094-60.97793075 30.63625506-96.62203518 80.12559015-102.21904332 147.87884654-5.74429781 70.25718107 21.50429441 127.9947387 81.15661796 165.70089876 63.92372449 40.35737445 130.64595307 76.44334797 196.04257446 114.44408742 3.82953187 2.20934532 7.36448439 4.12411126 11.78317501 1.47289688 25.33382629-14.72896878 50.66765259-29.45793756 77.47437578-44.92335477-5.59700814-3.38766283-9.42654002-5.89158752-13.40336158-8.24822253-71.2882089-41.24111259-142.42912809-82.62951485-214.01191636-123.28146868-16.05457598-9.13196064-22.53532223-21.06242535-22.38803255-39.32634664 0.58915875-72.31923671 0.29457937-144.7857631 0.29457937-217.98873792z m190.15098694 130.79324274c6.1861669-3.24037312 10.31027814-5.30242877 14.28709973-7.65906375 70.55176044-40.65195383 141.25081059-81.00932829 211.36070197-122.39773056 16.79102442-9.86840908 31.07812413-10.16298847 47.86914854-0.29457937 59.0631648 34.907656 118.56819867 68.78428419 177.92594284 102.95549177 3.5349525 2.06205563 6.48074626 5.89158752 12.51962347 4.12411125 7.80635344-70.10989139-13.40336159-129.32034589-70.69905013-171.73977596-59.50503387-44.18690634-125.34352431-50.22578353-191.03472509-15.6127069-65.98578013 34.907656-129.76221494 74.08671296-194.56967756 111.35100396-3.68224221 2.06205563-7.65906378 3.82953187-7.65906377 9.27925033v89.99399923z m208.41490823 115.9169843c0-20.17868721-0.44186907-36.96971163 0.14728969-53.61344636 0.29457937-7.65906378-2.20934532-12.0777544-8.98467096-15.75999658-29.45793756-16.49644503-58.76858542-33.43475913-87.78465391-50.6676526-5.59700814-3.38766283-9.72111939-3.38766283-15.46541723-0.14728971-29.01606849 17.23289347-58.32671636 34.17120756-87.78465393 50.66765261-6.77532564 3.82953187-8.83738127 8.24822251-8.83738127 15.90728628 0.44186907 32.84560039 0.58915875 65.83849045 0 98.68409082-0.1472897 8.98467097 3.09308345 13.69794096 10.75214722 17.67476253 13.99252034 7.2171947 27.24859225 15.61270692 40.94653321 23.41906036 17.38018317 9.86840908 34.7603663 27.6904613 51.99325978 27.54317163 17.96934191-0.1472897 35.79139413-17.52747284 53.46615667-27.54317163 16.9383141-9.72111939 39.17905696-16.20186565 48.75288666-30.78354473 9.86840908-15.46541722 1.76747625-39.03176725 2.79850407-55.38092262z" fill="#ab68ff" p-id="7822"></path></svg>')

const me = ref('<svg t="1757946975235" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="14777" width="14" height="14"><path d="M0 0h1024v1024H0V0z" fill="#202425" opacity=".01" p-id="14778"></path><path d="M34.133333 819.2a204.8 204.8 0 0 1 204.8-204.8h546.133334a204.8 204.8 0 0 1 204.8 204.8v102.4a34.133333 34.133333 0 0 1-34.133334 34.133333H68.266667a34.133333 34.133333 0 0 1-34.133334-34.133333v-102.4z" fill="#11AA66" p-id="14779"></path><path d="M512 68.266667a204.8 204.8 0 0 1 204.8 204.8v68.266666a204.8 204.8 0 0 1-409.6 0V273.066667a204.8 204.8 0 0 1 204.8-204.8z" fill="#FFAA44" p-id="14780"></path></svg>')

const sendChat0 = () => {
  const source = new EventSource(`/demo/mcp-ui/rag/chat?message=${encodeURIComponent(form.message)}`);
  form.message = ''

  source.addEventListener('open', () => {
    showResult.value = true
    // 重置或初始化showContent，加入图标
    if (showContent.value != '') {
      showContent.value += "\n\n\n\n"; // 与历史内容分隔
    }
    showContent.value += icon.value + "\n\n"; // 加入图标
    renderedHTML.value = md.render(showContent.value);
  })

  // 监听消息事件
  source.addEventListener('message', (event: any) => {
    // showContent.value += (event.data == '' ? ' ' : event.data)
    showContent.value += event.data
    renderedHTML.value = md.render(showContent.value)
  });

  // 监听完成事件
  source.addEventListener('complete', () => {
    source.close();
    renderedHTML.value = md.render(showContent.value);
  });

  // 监听错误事件
  source.addEventListener('error', (error: any) => {
    source.close();
  });
}

const contentRef = ref<any>(null)
watch(renderedHTML, async () => {
  await nextTick() // 等 DOM 更新
  if (contentRef.value) {
    contentRef.value.scrollTop = contentRef.value.scrollHeight
  }
})

const inputRef = ref<InputInstance>();

function enterEvent(event: KeyboardEvent) {
  if (event.key === 'Enter') {
    event.preventDefault();
    if (form.message == '') {
      inputRef.value?.focus()
      return
    }
    showResult.value = true
    // 重置或初始化showContent，加入图标
    if (showContent.value != '') {
      showContent.value += "\n\n\n\n"; // 与历史内容分隔
    }
    showContent.value += me.value + "\n\n"; // 加入图标
    showContent.value += form.message;
    renderedHTML.value = md.render(showContent.value);
    sendChat0()
  }
}

const drawer = ref(false)
const drawerTitle = ref('导入知识库')
import type {UploadInstance} from 'element-plus'

const importForm = reactive({
  files: [] as any[],
  defaultSystem: ''
})

const uploadRef = ref<UploadInstance>()
const fileChange = (uploadFile: UploadFile) => {
  importForm.files.push(uploadFile.raw)
  console.log("选择文件: ", uploadFile.raw)
}
const fileRemove = (uploadFile: UploadFile) => {
  importForm.files.length=0
  console.log("选择文件: ", uploadFile.raw)
}
const fileImport = () => {
  if (importForm.files.length==0) {
    msg('导入文件为空', 'warning')
    return
  }
  const formData = new FormData();
  importForm.files.forEach((file: any) => formData.append("files", file))
  formData.append("defaultSystem", importForm.defaultSystem)
  axios({
    url: '/knowledge/import',
    method: 'post',
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data"
    }
  }).then((res: any) => {
    if (res.data.state == 'OK') {
      msg(res.data.body, 'success')
      uploadRef.value!.clearFiles()
    } else {
      let content = res.config.baseURL + res.config.url + ': ' + res.data.errorMessage;
      msg(content, "warning")
      uploadRef.value!.clearFiles()
    }
  }).catch((err: Error) => {
    console.log('', err)
    msg(langData.axiosRequestErr, 'error')
    uploadRef.value!.clearFiles()
  })
}

</script>

<template>
  <div class="container">
    <div style="position:absolute; right: 5px; cursor: pointer" title="导入知识库" @click="drawer=true">
      <svg t="1758599010419" class="icon" viewBox="0 0 1389 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
           p-id="8272" width="20" height="20">
        <path
            d="M508.854857 263.533714S424.813714 172.178286 310.857143 246.784C208.786286 317.988571 226.742857 448.548571 226.742857 448.548571S0 498.322286 0 759.881143c5.12 260.900571 246.125714 263.643429 246.125714 263.643428l361.837715 0.475429v-279.588571h-173.385143l260.388571-279.405715 260.388572 279.405715h-173.714286v279.405714l351.451428-0.475429s224.438857 0.182857 255.926858-247.990857c15.030857-271.542857-216.978286-325.046857-216.978286-325.046857s26.331429-402.066286-299.154286-447.817143c-279.186286-32.256-364.032 261.046857-364.032 261.046857z"
            fill="#439742" p-id="8273"></path>
      </svg>
    </div>
    <div
        v-html="renderedHTML"
        class="markdown-content"
        ref="contentRef"
    ></div>

    <!-- 输入框区域 - 固定在底部居中 -->
    <div class="input-container">
      <el-form
          :model="form"
          size="small"
          label-position="right"
          inline-message
          :inline="false"
          label-width="0%"
      >
        <el-form-item label="" prop="message">
          <el-input
              v-model="form.message"
              type="textarea"
              :rows="2"
              clearable
              ref="inputRef"
              @keydown="enterEvent"
              class="my-textarea"
              placeholder="提出任何你想要的问题"
          />
        </el-form-item>
      </el-form>
    </div>
  </div>
  <el-drawer
      v-model="drawer"
      :title="drawerTitle"
      direction="rtl"
      size="40%"
  >
    <el-form :model="importForm" label-position="left" size="small" :inline="false" label-width="25%">
      <el-form-item label-width="0%">
        <el-icon style="cursor: pointer;" :size="20" @click="fileImport()">
          <template #default>
            <svg t="1758602807885" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="9504" width="32" height="32"><path d="M391.4 354.1s-54.2-56.7-127.7-10.4c-65.8 44.2-54.2 125.2-54.2 125.2S63.3 499.8 63.3 662.1C66.6 824 222 825.7 222 825.7l233.3 0.3V652.5H343.5l167.9-173.4 167.9 173.4h-112v173.4l226.6-0.3s144.7 0.1 165-153.9C968.6 503.2 819 470 819 470s17-249.5-192.9-277.9c-180-20-234.7 162-234.7 162z" fill="#00A4BD" p-id="9505"></path></svg>
          </template>
        </el-icon>
        <el-icon @click="uploadRef?.clearFiles()" style="cursor: pointer; margin-left: 10px" :size="20">
          <template #default>
            <svg t="1758602883110" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="11547" width="32" height="32"><path d="M700.94 271.28l-54.96 67.68L932 365.48 846.5 92l-67.98 83.76A430.2 430.2 0 0 0 522.8 92C284.9 92 92 284.36 92 521.6c0 237.29999999 192.9 429.6 430.8 429.6a430.92 430.92 0 0 0 402.6-276.3 61.32 61.32 0 0 0-35.46-79.20000001 61.62 61.62 0 0 0-79.5 35.40000001 307.8 307.8 0 0 1-287.58 197.4c-169.98 0-307.8-137.4-307.80000001-306.9 0-169.43999999 137.81999999-306.84 307.80000001-306.84 65.04 0 126.78 20.16 178.08 56.52" fill="#bfbfbf" p-id="11548"></path></svg>
          </template>
        </el-icon>
      </el-form-item>
      <el-form-item label="" prop="" label-width="0%">
        <el-input v-model="importForm.defaultSystem" placeholder="给的知识库输入一段功能描述" type="textarea" :rows="5"
                  style="width: 100%"/>
      </el-form-item>
      <el-form-item label-width="0%">
        <el-upload
            action="#"
            name="file"
            ref="uploadRef"
            class="upload-demo"
            drag
            :multiple="true"
            :auto-upload="false"
            :limit="10"
            @change="fileChange"
            @remove="fileRemove"
            accept=".pdf,.txt"
            style="height:230px;width: 100%"
        >
          <el-icon class="el-icon--upload">
            <upload-filled/>
          </el-icon>
          <div class="el-upload__text">
            Drop file here or <em> click to upload </em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              pdf/txt files with a size less than 100mb
            </div>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>
  </el-drawer>
</template>

<style scoped>
.container {
  /* 使用相对定位作为输入框的定位参考 */
  position: absolute;
  flex-grow: 1;
  padding: 10px;
  width: 98%;
  /* 容器高度 = 视窗高度 - 顶部预留空间 */
  height: calc(100vh - 40px);
  overflow: hidden;
  border: 0px solid red;
}

.markdown-content {
  padding: 20px;
  border: 0px solid #ccc;
  border-radius: 4px;
  font-family: "Times New Roman", Times, serif;
  font-size: 0.6em;
  line-height: 1.5;
  width: 96%;
  box-sizing: border-box; /* 确保padding不影响宽度计算 */
  word-wrap: break-word;
  white-space: normal;
  overflow-wrap: break-word;
  height: calc(100% - 105px);
  overflow-y: auto;
  overflow-x: hidden;
}

/* 输入框容器 - 固定在底部并居中 */
.input-container {
  position: absolute;
  bottom: 0px; /* 距离视窗底部10px */
  width: 50%; /* 适应容器宽度 */
  left: 25%;
  z-index: 100; /* 保证在内容区域上方 */
  height: 55px;
  box-sizing: border-box;
}

.markdown-content svg {
  display: inline-block;
}

.my-textarea {
  border: 2px solid #ADD3FF; /* 边框加粗到2px，颜色为深灰 */
  border-radius: 8px; /* 可选：圆角 */
  padding: 0px; /* 内边距 */
  outline: none; /* 聚焦时去掉默认外边框 */
  width: 100%;
}
</style>
