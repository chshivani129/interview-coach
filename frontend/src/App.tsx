import { useState } from 'react'
import './App.css'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "${API_BASE_URL}";

interface JobResponse {
  jobId: number
  title: string
  seniority: string
  skills: string[]
  questionCount: number
}

interface Question {
  id: number
  text: string
  category: string
  skill: string
}

interface Evaluation {
  attemptId: number
  questionId: number
  clarity: number
  specificity: number
  depth: number
  overallScore: number
  feedback: string
  improvementTip: string
  supportingQuotes: string[]
  latencyMs: number
}

function App() {
  const [jobDescription, setJobDescription] = useState('')
  const [job, setJob] = useState<JobResponse | null>(null)
  const [questions, setQuestions] = useState<Question[]>([])
  const [answers, setAnswers] = useState<Record<number, string>>({})
  const [evaluations, setEvaluations] = useState<Record<number, Evaluation>>({})
  const [loading, setLoading] = useState(false)
  const [submitting, setSubmitting] = useState<number | null>(null)
  const [error, setError] = useState('')

  const generateInterview = async () => {
    if (!jobDescription.trim()) {
      setError('Please enter a job description first.')
      return
    }

    setLoading(true)
    setError('')
    setJob(null)
    setQuestions([])
    setAnswers({})
    setEvaluations({})

    try {
      const response = await fetch('${API_BASE_URL}/api/jobs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ jobDescription }),
      })

      if (!response.ok) throw new Error('Unable to generate interview.')

      const jobData: JobResponse = await response.json()
      setJob(jobData)

      const questionResponse = await fetch(
        `${API_BASE_URL}/api/jobs/${jobData.jobId}/questions`
      )

      if (!questionResponse.ok) throw new Error('Unable to load questions.')

      setQuestions(await questionResponse.json())
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Something went wrong.')
    } finally {
      setLoading(false)
    }
  }

  const submitAnswer = async (questionId: number) => {
    const answer = answers[questionId]?.trim()

    if (!answer) {
      setError('Please enter an answer before submitting.')
      return
    }

    setSubmitting(questionId)
    setError('')

    try {
      const response = await fetch(
        `${API_BASE_URL}/api/questions/${questionId}/answers`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ answer }),
        }
      )

      if (!response.ok) throw new Error('Unable to evaluate your answer.')

      const evaluation: Evaluation = await response.json()

      setEvaluations((previous) => ({
        ...previous,
        [questionId]: evaluation,
      }))
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Something went wrong.')
    } finally {
      setSubmitting(null)
    }
  }

  return (
    <div className="app">
      <header className="navbar">
        <div className="brand">
          <div className="brand-icon">✦</div>
          <span>Interview<span className="brand-highlight">IQ</span></span>
        </div>
        <div className="nav-label">AI-POWERED INTERVIEW PREPARATION</div>
        <div className="status">
          <span className="status-dot"></span>
          Gemini AI connected
        </div>
      </header>

      <main className="main-content">
        <section className="hero">
          <div className="eyebrow">YOUR PERSONAL AI INTERVIEW COACH</div>
          <h1>Prepare smarter.<br /><span>Interview confidently.</span></h1>
          <p>
            Turn any job description into a personalized interview practice
            session. Get AI-generated questions and actionable feedback.
          </p>
        </section>

        <section className="input-card">
          <div className="card-heading">
            <div>
              <div className="step-label">STEP 01</div>
              <h2>Start with a job description</h2>
              <p>Paste the job description you're preparing for.</p>
            </div>
            <div className="document-icon">▤</div>
          </div>

          <textarea
            value={jobDescription}
            onChange={(e) => setJobDescription(e.target.value)}
            placeholder="Paste the job description here..."
          />

          <div className="form-footer">
            <span>{jobDescription.length} characters</span>
            <button
              onClick={generateInterview}
              disabled={loading || !jobDescription.trim()}
            >
              {loading ? 'Generating with Gemini...' : '✦ Generate Interview →'}
            </button>
          </div>

          {error && <div className="error-message">{error}</div>}
        </section>

        {job && (
          <section className="results-section">
            <div className="results-heading">
              <div>
                <div className="step-label">STEP 02 · AI ANALYSIS COMPLETE</div>
                <h2>Your personalized interview</h2>
              </div>
              <span className="generated-badge">✦ Generated by Gemini</span>
            </div>

            <div className="job-summary">
              <div>
                <span className="summary-label">JOB TITLE</span>
                <h3>{job.title}</h3>
              </div>
              <div>
                <span className="summary-label">EXPERIENCE LEVEL</span>
                <h3>{job.seniority}</h3>
              </div>
              <div>
                <span className="summary-label">QUESTIONS</span>
                <h3>{job.questionCount}</h3>
              </div>
            </div>

            <div className="skills-section">
              <h3>Identified skills</h3>
              <div className="skills-list">
                {job.skills.map((skill) => (
                  <span className="skill-tag" key={skill}>{skill}</span>
                ))}
              </div>
            </div>

            <div className="questions-heading">
              <h3>Interview questions</h3>
              <span>{questions.length} questions</span>
            </div>

            <div className="question-list">
              {questions.map((question, index) => {
                const evaluation = evaluations[question.id]

                return (
                  <article className="question-card" key={question.id}>
                    <div className="question-number">
                      {String(index + 1).padStart(2, '0')}
                    </div>

                    <div className="question-content">
                      <p>{question.text}</p>

                      <div className="question-tags">
                        <span>{question.skill}</span>
                        <span>{question.category}</span>
                      </div>

                      <textarea
                        className="answer-input"
                        placeholder="Write your interview answer here..."
                        value={answers[question.id] || ''}
                        onChange={(e) =>
                          setAnswers((previous) => ({
                            ...previous,
                            [question.id]: e.target.value,
                          }))
                        }
                      />

                      <button
                        className="evaluate-button"
                        disabled={submitting !== null}
                        onClick={() => submitAnswer(question.id)}
                      >
                        {submitting === question.id
                          ? 'Gemini is evaluating...'
                          : evaluation
                            ? '↻ Evaluate Again'
                            : '✦ Evaluate My Answer'}
                      </button>

                      {evaluation && (
                        <div className="evaluation-panel">
                          <div className="evaluation-title">
                            <h3>AI Evaluation</h3>
                            <span>
                              Overall: {evaluation.overallScore.toFixed(1)}/5
                            </span>
                          </div>

                          <div className="score-grid">
                            <div className="score-box">
                              <span>Clarity</span>
                              <strong>{evaluation.clarity}/5</strong>
                            </div>
                            <div className="score-box">
                              <span>Specificity</span>
                              <strong>{evaluation.specificity}/5</strong>
                            </div>
                            <div className="score-box">
                              <span>Depth</span>
                              <strong>{evaluation.depth}/5</strong>
                            </div>
                          </div>

                          <div className="feedback-box">
                            <h4>Feedback</h4>
                            <p>{evaluation.feedback}</p>
                          </div>

                          <div className="feedback-box improvement-box">
                            <h4>How to improve</h4>
                            <p>{evaluation.improvementTip}</p>
                          </div>

                          {evaluation.supportingQuotes.length > 0 && (
                            <div className="feedback-box">
                              <h4>Supporting quotes from your answer</h4>
                              {evaluation.supportingQuotes.map((quote, i) => (
                                <blockquote key={i}>"{quote}"</blockquote>
                              ))}
                            </div>
                          )}

                          <div className="latency">
                            Evaluated by Gemini · {evaluation.latencyMs} ms
                          </div>
                        </div>
                      )}
                    </div>
                  </article>
                )
              })}
            </div>
          </section>
        )}
      </main>

      <footer>
        Built with React, Spring Boot, PostgreSQL & Gemini AI
      </footer>
    </div>
  )
}

export default App