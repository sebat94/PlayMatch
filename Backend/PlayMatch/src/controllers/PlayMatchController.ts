import { conexion } from "./bdconfig";
import { GenericErrorResponse } from "../utils/GenericErrorResponse";
import { IAnswer } from "../interfaces/IAnswer";

export class PlayMatchController {

    /**
     * GET ALL Questions
     */
    static listAllQuestions(): Promise<[{id: number, question: string}]>{
        return new Promise((resolve, reject) => {
            conexion.query('SELECT * FROM preguntas', (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve(result);
            });
        });
    }

    /**
     * SAVE ALL Answers
     * @param answers 
     */
    static insertAllAnswers(answers: IAnswer[]): Promise<any>{
        // Convert  [{fk_from, fk_to, question, answer}, {...}, {...}]  To  [[fk_from, fk_to, question, answer], [...], [...]]  Because this MySql only can manage anidated arrays to multiple inserts.
        let result = [];
        let fromId;
        let toId;
        answers.forEach((eachAnswer: IAnswer, i: number, arr: IAnswer[]) => {
            result.push([eachAnswer.fk_from, eachAnswer.fk_to, eachAnswer.question, eachAnswer.answer]);
            // Guardamos en la última comprobación los IDs
            if(arr.length == i -1){
                fromId = eachAnswer.fk_from;
                toId = eachAnswer.fk_to;
            }
        });
        return new Promise((resolve, reject) => {
            conexion.query('INSERT INTO respuestas_usuarios (fk_from, fk_to, question, answer) VALUES ?', [result], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else    //  Return Coincidences
                    this.getResultsOfMatch(fromId, toId)
                    .then((results: any) => resolve(results))
                    .catch(error => reject(error));
            });
        });
    }

    /**
     * GET RESULTS OF MATCH     // TODO: Controlar, que antes de devolver los resultados ambas partes deben haber finalizado el juego.
     */
    static getResultsOfMatch(userMe: number, userOpposite: number): Promise<any>{
        return new Promise((resolve, reject) => {
            conexion.query('SELECT r1.answer ' +
                            'FROM respuestas_usuarios r1 ' +
                            'JOIN respuestas_usuarios r2 ' +
                            'ON r1.answer = r2.answer ' +
                            'WHERE r1.fk_from = ? AND r1.fk_to = ? ' +
                            'And r2.fk_from = ? AND r2.fk_to = ? ' +
                            'GROUP BY r1.answer, r2.answer', [userMe, userOpposite, userOpposite, userMe], (error, result, fields) => {
                if(error)
                    return reject(GenericErrorResponse.getMySqlErrorMessage(error));
                else
                    return resolve(result);
            });
        });
    }

}