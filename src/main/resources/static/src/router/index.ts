import {createRouter, createWebHistory, createWebHashHistory} from 'vue-router'

const router = createRouter({
    history: createWebHashHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/mcp',
            name: 'mcp',
            component: () => import('@/views/MCP.vue'),
        },
        {
            path: '/rag',
            name: 'rag',
            component: () => import('@/views/RAG.vue'),
        }
    ],
})

export default router
