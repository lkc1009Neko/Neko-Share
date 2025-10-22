<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
    target: HTMLElement | null
}>()

interface DirectoryItem {
    tag: string
    name: string
    index: number
    children: DirectoryItem[]
}

let map = new Map<number, number>()
const directory = ref<DirectoryItem[]>([])
let index = 0
let stack: DirectoryItem[] = []

watch(() => props.target, (newVal) => {
    if (!newVal) return

    const headings = newVal?.querySelectorAll('h1, h2, h3, h4, h5, h6') as NodeListOf<HTMLHeadingElement> | null

    headings?.forEach((item: HTMLHeadingElement) => {
        const level = Number(item.tagName.substring(1))
        const currentObj: DirectoryItem = {
            tag: item.tagName,
            name: item.innerText,
            index: index++,
            children: []
        }

        // 出栈，直到找到当前级的父级或者栈空（当前级为栈顶）
        while (stack.length > 0 && Number(stack[stack.length - 1]!.tag.substring(1)) >= level) {
            stack.pop()
        }

        if (stack.length === 0) {
            directory.value.push(currentObj)
        } else {
            // 之前利用while出栈已经保证了栈尾就是当前级的父级（当然，如果只有一个栈顶，那他既是栈顶，也是栈尾）
            stack[stack.length - 1]!.children.push(currentObj)
        }
        stack.push(currentObj)
        // 这个记录是因为目前naive  ui的侧边导航需要href跳转（导致标题标签需要加id），而不是scrollTo定位，万一后续需要用scrollTo定位的话，这个map就派上用场了
        map.set(index, item.getBoundingClientRect().top + window.scrollY)
    })
})
</script>

<template>
    <div class="directory">
        <h2>目录</h2>
        <n-anchor show-rail show-background>
            <!-- h1 -->
            <n-anchor-link v-for="item in directory" :key="item.index" :href="'#heading-' + item.index" :title="item.name">
                <!-- h2 -->
                <n-anchor-link v-for="child in item.children" :key="child.index" :href="'#heading-' + child.index"
                    :title="child.name">
                    <!-- h3 -->
                    <n-anchor-link v-for="grandChild in child.children" :key="grandChild.index"
                        :href="'#heading-' + grandChild.index" :title="grandChild.name">
                        <!-- h4 -->
                        <n-anchor-link v-for="h4 in grandChild.children" :key="h4.index" :href="'#heading-' + h4.index"
                            :title="h4.name">
                            <!-- h5 -->
                            <n-anchor-link v-for="h5 in h4.children" :key="h5.index" :href="'#heading-' + h5.index"
                                :title="h5.name">
                                <!-- h6 -->
                                <n-anchor-link v-for="h6 in h5.children" :key="h6.index" :href="'#heading-' + h6.index"
                                    :title="h6.name" />
                            </n-anchor-link>
                        </n-anchor-link>
                    </n-anchor-link>
                </n-anchor-link>
            </n-anchor-link>
        </n-anchor>
    </div>
</template>