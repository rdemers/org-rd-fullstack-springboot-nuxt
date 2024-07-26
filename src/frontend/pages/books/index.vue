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
    <v-alert v-if=isErrorBook color="error" icon="$error" 
             :title="$t('book.alert.title')" :text="$t('book.alert.text')"/>
    <v-row>    
        <v-table theme="dark" density="compact">
            <thead>
                <tr>
                    <th class="text-left">{{$t('book.id')}}</th>
                    <th class="text-left">{{$t('book.title')}}</th>
                    <th class="text-left">{{$t('book.description')}}</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="book in books" :key="book.id">
                    <td>{{ book.id }}</td>
                    <td>{{ book.title }}</td>
                    <td>{{ book.description }}</td>
                    <td>
                        <v-btn tile outlined color="success" height="20" @click=navigateDetail(book.id)>
                            <v-icon left>mdi-pencil</v-icon>
                        </v-btn>
                    </td>
                    <td>
                        <v-btn tile outlined color="success" height="20" @click=navigateDelete(book.id)>
                            <v-icon left>mdi-trash-can-outline</v-icon>
                        </v-btn>
                    </td>
                </tr>
            </tbody>
        </v-table>
    </v-row>
    <v-row>
        <v-btn tile outlined color="success" height="20" @click=navigateAdd()>
            <v-icon left>mdi-pencil-plus</v-icon>
        </v-btn>
        <v-btn tile outlined color="success" height="20" @click=navigatePdf()>
            <v-icon left>mdi-file-pdf-box</v-icon>
        </v-btn>
        <v-btn tile outlined color="error" height="20" @click=navigateError()>
            <v-icon left>mdi-bomb</v-icon>
        </v-btn>
    </v-row>
    <v-dialog v-model="deleteDialog" persistent width="auto">
        <v-card>
            <v-card-text>{{$t('book.askdelete')}}</v-card-text>
            <v-card-actions>
                <v-spacer/>
                <v-btn color="success" class="mx-2" flat @click="deleteDialog = false">{{$t('common.button.cancel')}}</v-btn>
                <v-btn color="success" class="mx-2" flat @click=deleteBook()>{{$t('common.button.ok')}}</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
    <v-dialog v-model="pdfDialog" persistent width="900" height="600">
        <v-card>
            <vrd-pdfviewer url="http://localhost:8080/report/books-report"/>
            <v-card-actions>
                <v-spacer/>
                <v-btn color="success" class="mx-2" flat @click="pdfDialog = false">{{$t('common.button.ok')}}</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>
  
<script setup lang="ts">
    import { onMounted, ref } from "vue";

    import type Book         from "@/types/Book"
    import type ResponseData from "@/types/ResponseData";

    import BookService       from "@/services/BookService";

    const books        = ref<Book[]>();
    const isErrorBook  = ref<boolean>(false);
    const deleteDialog = ref<boolean>(false);
    const pdfDialog    = ref<boolean>(false);

    let bookIDtoDelete:string = "";

    onMounted((): void => {
        retrieveBooks();
    })

    function retrieveBooks(): void {
        BookService.getAll()
            .then((response: ResponseData) => {
                books.value = response.data;
                isErrorBook.value = false;
            })
            .catch((e: Error) => {
                isErrorBook.value = true;
                console.log(e);
            });                
    }
    
    function navigateDetail(id:string): any {
        return navigateTo("/books/book/" + id);
    }

    function navigateDelete(id:string): void {
        bookIDtoDelete = id;
        deleteDialog.value = true;
    }

    function navigatePdf(): void {
        pdfDialog.value = true;
    }

    function navigateError(): any {
        return navigateTo("/books/book/invalid_url_show_error_page");
    }

    function deleteBook(): void {
        deleteDialog.value = false;
        BookService.delete(bookIDtoDelete)
            .then((response: ResponseData) => {
                books.value = response.data;
                isErrorBook.value = false;

                bookIDtoDelete = "";
                retrieveBooks(); // Refresh.
            })
            .catch((e: Error) => {
                isErrorBook.value = true;
                console.log(e);
            });                
    }            

    function navigateAdd(): any {
        return navigateTo("/books/book/add");
    } 
</script>
  
<style scoped>
</style>