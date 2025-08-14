/*
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
 */

import type Book        from "../types/Book";
import type Session     from "../types/Session";

import { bookApi }      from "../services/HTTPService";
import { SessionStore } from "../store/SessionStore";

class BookService {

    public getAll() : Promise<any> {
        const session:Session = SessionStore().getSession;
        return bookApi.get("/books", {
            headers: {
                "authorization": session.jwtToken
            }
        });
    }

    public get(id: string) : Promise<any> {
        const session:Session = SessionStore().getSession;
        return bookApi.get("/books/" + id, {
            headers: {
                "authorization": session.jwtToken
            }
        });
    }

    public create(data: Book) : Promise<any> {
        const session:Session = SessionStore().getSession;
        return bookApi.post("/books", data, {
            headers: {
                "authorization": session.jwtToken
            }
        });
    }

    public update(book: Book) : Promise<any> {
        const session:Session = SessionStore().getSession;
        return bookApi.put("/books/" + book.id, book, {
            headers: {
                "authorization": session.jwtToken
            }
        });
    }

    public delete(id: string) : Promise<any> {
        const session:Session = SessionStore().getSession;
        return bookApi.delete("/books/" + id, {
            headers: {
                "authorization": session.jwtToken
            }
        });
    }

    public deleteAll() : Promise<any> {
        const session:Session = SessionStore().getSession;
        return bookApi.delete("/books", {
            headers: {
                "authorization": session.jwtToken
            }
        });
    }

    public findByTitle(title: string) : Promise<any> {
        const session:Session = SessionStore().getSession;
        return bookApi.get("/books?title=" + title, {
            headers: {
                "authorization": session.jwtToken
            }
        });
    }
}

const bookService: BookService = new BookService();
export default bookService;