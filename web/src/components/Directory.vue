<script setup lang="ts">

import { ref, watch, h } from 'vue'

import { NAnchor, NAnchorLink } from 'naive-ui'
import type { VNode } from 'vue'

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

    directory.value = []
    stack = []
    index = 0
    map.clear()

    if (!newVal) {
        return
    }

    const headings = newVal?.querySelectorAll('h1, h2, h3, h4, h5, h6') as NodeListOf<HTMLHeadingElement> | null

    headings?.forEach((item: HTMLHeadingElement) => {

        item.id = `heading-${index}`;

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

const renderAnchorLink = (items: DirectoryItem[]): VNode[] => {
        return items.map((item) =>
            h(
                NAnchorLink,
                { href: '#heading-' + item.index, title: item.name },
                item.children.length > 0 ? () => renderAnchorLink(item.children) : []
            )
        )
    }

    const renderDirectory = {
        render() {
            return renderAnchorLink(directory.value)
        }
    }

</script>

<template>
    <div>
        <h2>目录</h2>
        <n-anchor show-rail show-background>
            <component :is="renderDirectory" />
        </n-anchor>
    </div>
</template>

<style scoped></style>