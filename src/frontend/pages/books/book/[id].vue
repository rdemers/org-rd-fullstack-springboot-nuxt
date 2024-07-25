<!--
  * Copyright 2023, 2024; Réal Demers.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *    http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  -->
<template>
    <v-alert v-if="isErrorBook" color="error" icon="$error" 
             :title="$t('book.alert.title')" :text="$t('book.alert.text')"/>
    <v-form ref="bookForm" v-if="!isErrorBook" v-model="isValid" @submit.prevent="updateBook()">
        <v-text-field :label="$t('book.id')"          v-model="book.id"          :disabled="true"/>
        <v-text-field :label="$t('book.title')"       v-model="book.title"       :rules="[ruleRequired($t('book.title')), ruleMinLength($t('book.title'), 4)]"/>
        <v-text-field :label="$t('book.description')" v-model="book.description" :rules="[ruleRequired($t('book.description')), ruleMinLength($t('book.description'), 10)]"/>
        <v-spacer/>
        <v-btn color="success" height="20" class="mx-2" flat @click=navigateToBooks()>{{$t('common.button.cancel')}}</v-btn>
        <v-btn color="success" height="20" class="mx-2" flat type="submit" :disabled="!isValid">{{$t('common.button.ok')}}</v-btn>
    </v-form>
</template>

<script setup lang="ts">
    import { onMounted, ref } from "vue";

    import type Book         from "@/types/Book";
    import type ResponseData from "@/types/ResponseData";

    import BookService       from "@/services/BookService";
  
    definePageMeta({
        validate: async (route) => {
            return /^\d+$/.test(route.params.id as string)
        }
    })

    const { t } = useI18n();
    const isValid = ref<boolean>(false);
    const isErrorBook = ref<boolean>(false);
    const book = ref<Book>({id: "0", title:"new Title", description: "new Description"});

    onMounted((): void => {
        const route = useRoute();
        getBook(route.params.id as string);
    })

    function ruleRequired(propertyType:any): any {
        return (v:string) => 
            (v && v.length > 0 || t('book.rule.rule1') + propertyType + ".");
    }

    function ruleMinLength(propertyType:string, minLength:number): any {
        return (v:string) => 
            (v && v.length >= minLength || propertyType + t('book.rule.rule2-0') + minLength + t('book.rule.rule2-1'));
    }

    function getBook(id: string) : void {
        BookService.get(id)
            .then((response: ResponseData) => {
                isErrorBook.value = false;
                book.value = response.data;
            })
            .catch((e: Error) => {
                isErrorBook.value = true;
            });
    }

    function updateBook() : void {
        BookService.update(book.value as Book)
            .then((response: ResponseData) => {
                isErrorBook.value = false;
                navigateToBooks();
            })
            .catch((e: Error) => {
                isErrorBook.value = true;
            })
            .finally(() => {
                console.log("Finally ...");
            });
    }

    function navigateToBooks() : any {
        return navigateTo("/books");
    }
</script>
  
<style scoped>
</style>