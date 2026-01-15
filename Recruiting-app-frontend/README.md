# Recruiting App Frontend

![License](https://img.shields.io/badge/license-MIT-blue.svg) ![Version](https://img.shields.io/badge/version-0.1.0-blue.svg)

A modern, responsive web application for managing recruitment workflows. Built with **Next.js**, **React**, **Tailwind CSS**, and **TypeScript**, this frontend provides a sleek, premium user experience for posting jobs, reviewing candidates, and tracking hiring metrics.

---

## ✨ Features

- **Dynamic job board** – browse, filter, and search open positions.
- **Candidate management** – view profiles, resumes, and interview statuses.
- **Real‑time notifications** – get instant updates on application activity.
- **Responsive design** – looks great on desktop, tablet, and mobile.
- **Dark mode & glass‑morphism UI** – premium visual aesthetics with smooth micro‑animations.
- **Internationalisation ready** – easy to add multiple languages.

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|------------|
| Framework | **Next.js 14 (React 18)** |
| Language | **TypeScript** |
| Styling | **Tailwind CSS** (custom design tokens) |
| State Management | **React Context / Zustand** |
| Icons | **Heroicons** |
| Testing | **Jest** + **React Testing Library** |
| Linting | **ESLint** + **Prettier** |

---

## 📦 Installation

```bash
# Clone the repository (if you haven't already)
git clone <repository-url>
cd Recruiting-app-frontend

# Install dependencies
npm ci   # uses the lockfile for reproducible installs
```

> **Note:** The `npm ci` command ensures a clean, deterministic install. If you prefer `npm install`, you can run it, but `npm ci` is recommended for CI/CD pipelines.

---

## 🚀 Development

Start the development server with hot‑module replacement:

```bash
npm run dev
```

Open your browser at `http://localhost:3000`. The app will automatically reload as you edit source files.

### Available Scripts

| Script | Description |
|--------|-------------|
| `dev` | Launch Next.js dev server (`next dev`) |
| `build` | Create an optimized production build (`next build`) |
| `start` | Run the production build locally (`next start`) |
| `test` | Run unit and component tests |
| `lint` | Run ESLint + Prettier checks |

---

## 📦 Production Build

When you’re ready to deploy:

```bash
npm run build
npm start   # or deploy the `out/` folder to Vercel/Netlify
```

The compiled assets will be placed in the `.next/` folder, ready to be served by any Node.js server or a platform like **Vercel**.

---

## 🧪 Testing

Run the test suite in watch mode:

```bash
npm run test
```

For a single run with coverage:

```bash
npm run test -- --coverage
```

---

## 🎨 Customising the Design System

The UI follows a **design‑system** approach using Tailwind’s configuration file (`tailwind.config.js`).

- **Colors:** Defined in the `theme.extend.colors` section – adjust HSL values for a harmonious palette.
- **Typography:** Uses the **Inter** font from Google Fonts – modify `fontFamily` to switch fonts.
- **Animations:** Subtle micro‑animations are defined under `theme.extend.keyframes` and applied via utility classes like `animate-fade-in`.

Refer to `src/styles/` for the full set of reusable component classes.

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/awesome-feature`).
3. Ensure linting and tests pass (`npm run lint && npm run test`).
4. Open a Pull Request with a clear description of your changes.

---

## 📄 License

This project is licensed under the **MIT License** – see the `LICENSE` file for details.

---

## 📞 Contact

For questions or feedback, reach out to the project maintainer:

- **Name:** Jane Doe
- **Email:** jane.doe@example.com
- **Twitter:** [@janedoe_dev](https://twitter.com/janedoe_dev)

Happy coding! 🎉
