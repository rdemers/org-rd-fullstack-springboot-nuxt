<!--
  * Copyright 2023; Réal Demers.
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
    <v-row align="center" justify="center">
        <v-card max-width="800">
            <v-card-title align="center" primary-title>{{$t('error.title')}}</v-card-title>
            <v-card-text align="center">
                <v-alert color="error" icon="$error" :title="$t('error.exception')" :text=message />
            </v-card-text>
            <v-card-actions>
                <v-spacer/>
                <v-btn align="center" color="primary" flat @click=handleError>{{$t('error.message')}}</v-btn>
            </v-card-actions>
        </v-card>
    </v-row>
</template>

<script lang="ts">
    import { defineComponent } from "vue";

    export default defineComponent({
        name: 'ExceptionLayout',
        layout: 'exception',
        props: {
            error: {
                type: Object,
                default: null
            }
        },

        setup(props) {

            const { t } = useI18n();

            // Determine the error message.
            let message: string = t('error.msg_error');
            if (props.error.statusCode === "404") {
                message = t('error.msg_cause');;
            }

            function handleError() {
                clearError({ redirect: '/' });
            }

            return {
                // Non-reactive - Const.
                message,

                // functions.
                handleError
            } 
        }
    });
</script>

<style scoped>
</style>
